package com.fasterxml.jackson.module.kotlin.annotation_introspector

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.introspect.Annotated
import com.fasterxml.jackson.databind.introspect.AnnotatedMember
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector
import com.fasterxml.jackson.databind.util.Converter
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.ReflectionCache
import com.fasterxml.jackson.module.kotlin.deser.CollectionValueStrictNullChecksConverter
import com.fasterxml.jackson.module.kotlin.deser.MapValueStrictNullChecksConverter
import com.fasterxml.jackson.module.kotlin.deser.ValueClassUnboxConverter
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.creator.ValueParameter
import com.fasterxml.jackson.module.kotlin.findKmConstructor
import com.fasterxml.jackson.module.kotlin.findPropertyByGetter
import com.fasterxml.jackson.module.kotlin.isNullable
import com.fasterxml.jackson.module.kotlin.isUnboxableValueClass
import com.fasterxml.jackson.module.kotlin.reconstructClassOrNull
import com.fasterxml.jackson.module.kotlin.toSignature
import kotlinx.metadata.jvm.fieldSignature
import kotlinx.metadata.jvm.setterSignature
import kotlinx.metadata.jvm.signature
import java.lang.reflect.Constructor
import java.lang.reflect.Executable
import java.lang.reflect.Method
import java.lang.reflect.Modifier

// AnnotationIntrospector to be run after default AnnotationIntrospector
// (in most cases, JacksonAnnotationIntrospector).
// Original name: KotlinNamesAnnotationIntrospector
internal class KotlinFallbackAnnotationIntrospector(
    val module: KotlinModule,
    private val strictNullChecks: Boolean,
    private val cache: ReflectionCache
) : NopAnnotationIntrospector() {
    // since 2.4
    override fun findImplicitPropertyName(member: AnnotatedMember): String? = when (member) {
        is AnnotatedMethod -> if (member.parameterCount == 0) {
            cache.getKmClass(member.declaringClass)?.findPropertyByGetter(member.annotated)?.name
        } else {
            null
        }
        is AnnotatedParameter -> findKotlinParameterName(member)
        else -> null
    }

    private fun findKotlinParameterName(param: AnnotatedParameter): String? = when (val owner = param.owner.member) {
        is Constructor<*> -> cache.getKmClass(param.declaringClass)?.findKmConstructor(owner)?.valueParameters
        is Method ->
            owner.takeIf { _ -> Modifier.isStatic(owner.modifiers) }
                ?.let { _ ->
                    val companion = cache.getKmClass(param.declaringClass)?.companionObject ?: return@let null
                    val companionKmClass = owner.declaringClass.getDeclaredField(companion)
                        .type
                        .let { cache.getKmClass(it) }!!
                    val signature = owner.toSignature()

                    companionKmClass.functions.find { it.signature == signature }?.valueParameters
                }
        else -> null
    }?.let { it[param.index].name }

    // If it is not a property on Kotlin, it is not used to ser/deserialization
    override fun findPropertyAccess(ann: Annotated): JsonProperty.Access? = (ann as? AnnotatedMethod)?.let { _ ->
        cache.getKmClass(ann.declaringClass)?.let { kmClass ->
            val method = ann.annotated

            // By returning an illegal JsonProperty.Access, it is effectively ignore.
            when (method.parameters.size) {
                0 -> JsonProperty.Access.WRITE_ONLY.takeIf { kmClass.findPropertyByGetter(method) == null }
                1 -> {
                    val signature = method.toSignature()
                    JsonProperty.Access.READ_ONLY.takeIf { kmClass.properties.none { it.setterSignature == signature } }
                }
                else -> null
            }
        }
    }

    private fun getValueParameter(a: AnnotatedParameter): ValueParameter? =
        cache.valueCreatorFromJava(a.owner.annotated as Executable)?.let { it.valueParameters[a.index] }

    // returns Converter when the argument on Java is an unboxed value class
    override fun findDeserializationConverter(a: Annotated): Any? = (a as? AnnotatedParameter)?.let { param ->
        getValueParameter(param)?.let { valueParameter ->
            val rawType = a.rawType

            valueParameter.createValueClassUnboxConverterOrNull(rawType) ?: run {
                if (strictNullChecks) {
                    valueParameter.createStrictNullChecksConverterOrNull(a.type, rawType)
                } else {
                    null
                }
            }
        }
    }

    // Find a converter to handle the case where the getter returns an unboxed value from the value class.
    override fun findSerializationConverter(a: Annotated): Converter<*, *>? = (a as? AnnotatedMethod)?.let { _ ->
        cache.findValueClassReturnType(a)?.let { cache.getValueClassBoxConverter(a.rawReturnType, it) }
    }

    // Determine if the unbox result of value class is nullable
    // @see findNullSerializer
    private fun Class<*>.requireRebox(): Boolean =
        cache.getKmClass(this)!!.properties.first { it.fieldSignature != null }.returnType.isNullable()

    // Perform proper serialization even if the value wrapped by the value class is null.
    // If value is a non-null object type, it must not be reboxing.
    override fun findNullSerializer(am: Annotated): JsonSerializer<*>? = (am as? AnnotatedMethod)?.let { _ ->
        cache.findValueClassReturnType(am)
            ?.takeIf { it.requireRebox() }
            ?.let { cache.getValueClassBoxConverter(am.rawReturnType, it).delegatingSerializer }
    }
}

private fun ValueParameter.createValueClassUnboxConverterOrNull(rawType: Class<*>): ValueClassUnboxConverter<*>? {
    return type.reconstructClassOrNull()
        ?.takeIf { it.isUnboxableValueClass() && it != rawType }
        ?.let { ValueClassUnboxConverter(it) }
}

// If the collection type argument cannot be obtained, treat it as nullable
// @see com.fasterxml.jackson.module.kotlin._ported.test.StrictNullChecksTest#testListOfGenericWithNullValue
private fun ValueParameter.isNullishTypeAt(index: Int) = arguments.getOrNull(index)?.isNullable ?: true

private fun ValueParameter.createStrictNullChecksConverterOrNull(type: JavaType, rawType: Class<*>): Converter<*, *>? {
    @Suppress("UNCHECKED_CAST")
    return when {
        Array::class.java.isAssignableFrom(rawType) && !this.isNullishTypeAt(0) ->
            CollectionValueStrictNullChecksConverter.ForArray(type, this)
        Iterable::class.java.isAssignableFrom(rawType) && !this.isNullishTypeAt(0) ->
            CollectionValueStrictNullChecksConverter.ForIterable(type, this)
        Map::class.java.isAssignableFrom(rawType) && !this.isNullishTypeAt(1) ->
            MapValueStrictNullChecksConverter(type, this)
        else -> null
    }
}
