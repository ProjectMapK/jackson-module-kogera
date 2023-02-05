package com.fasterxml.jackson.module.kotlin.annotation_introspector

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.introspect.Annotated
import com.fasterxml.jackson.databind.introspect.AnnotatedMember
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer
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
import com.fasterxml.jackson.module.kotlin.ser.ValueClassBoxConverter
import com.fasterxml.jackson.module.kotlin.toSignature
import kotlinx.metadata.KmClass
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
    override fun findImplicitPropertyName(
        member: AnnotatedMember
    ): String? = cache.getKmClass(member.declaringClass)?.let { kmClass ->
        when (member) {
            is AnnotatedMethod -> kmClass.findPropertyByGetter(member.annotated)?.name
            is AnnotatedParameter -> findKotlinParameterName(member, kmClass)
            else -> null
        }
    }

    private fun findKotlinFactoryParameterName(
        declaringClass: Class<*>,
        kmClass: KmClass,
        member: Method,
        index: Int
    ) = kmClass.companionObject?.takeIf { _ -> Modifier.isStatic(member.modifiers) }?.let { companion ->
        val companionKmClass = declaringClass.getDeclaredField(companion)
            .type
            .let { cache.getKmClass(it) }!!
        val signature = member.toSignature()

        companionKmClass.functions.find { it.signature == signature }
            ?.let { it.valueParameters[index].name }
    }

    private fun findKotlinParameterName(param: AnnotatedParameter, kmClass: KmClass): String? {
        val declaringClass = param.declaringClass

        return when (val member = param.owner.member) {
            is Constructor<*> -> kmClass.findKmConstructor(member)?.let { it.valueParameters[param.index].name }
            is Method -> findKotlinFactoryParameterName(declaringClass, kmClass, member, param.index)
            else -> null
        }
    }

    // If it is not a property on Kotlin, it is not used to serialization
    override fun findPropertyAccess(ann: Annotated): JsonProperty.Access? = when (ann) {
        is AnnotatedMethod ->
            ann.annotated
                .takeIf { it.parameters.isEmpty() } // Ignore target is only getter
                ?.let { method ->
                    cache.getKmClass(method.declaringClass)?.let { kmClass ->
                        JsonProperty.Access.WRITE_ONLY.takeIf { kmClass.findPropertyByGetter(method) == null }
                    }
                }
        else -> null
    }

    // Ignored during deserialization if not a property
    override fun hasIgnoreMarker(m: AnnotatedMember): Boolean = (m as? AnnotatedMethod)?.member
        ?.takeIf { it.parameters.size == 1 && !Modifier.isStatic(it.modifiers) }
        ?.let { cache.getKmClass(it.declaringClass) }
        ?.let { kmClass ->
            val methodSignature = m.annotated.toSignature()
            kmClass.properties.none { it.setterSignature == methodSignature }
        } ?: false

    private fun getValueParameter(a: AnnotatedParameter): ValueParameter? =
        cache.valueCreatorFromJava(a.owner.annotated as Executable)?.let { it.valueParameters[a.index] }

    // returns Converter when the argument on Java is an unboxed value class
    override fun findDeserializationConverter(a: Annotated): Any? = (a as? AnnotatedParameter)?.let { param ->
        getValueParameter(param)?.let { valueParameter ->
            val rawType = a.rawType

            valueParameter.createValueClassUnboxConverterOrNull(rawType) ?: run {
                if (strictNullChecks) {
                    valueParameter.createStrictNullChecksConverterOrNull(rawType)
                } else {
                    null
                }
            }
        }
    }

    private fun AnnotatedMethod.findValueClassBoxConverter(
        takePredicate: (Class<*>) -> Boolean
    ): ValueClassBoxConverter<*, *>? {
        val getter = this.member.apply {
            // If the return value of the getter is a value class,
            // it will be serialized properly without doing anything.
            // TODO: Verify the case where a value class encompasses another value class.
            if (this.returnType.isUnboxableValueClass()) return null
        }
        val kotlinProperty = cache.getKmClass(getter.declaringClass)?.findPropertyByGetter(getter)

        // Since there was no way to directly determine whether returnType is a value class or not,
        // Class is restored and processed.
        // If the cost of this process is significant, consider caching it.
        return kotlinProperty?.returnType?.reconstructClassOrNull()?.let { clazz ->
            clazz.takeIf(takePredicate)
                ?.let { ValueClassBoxConverter(getter.returnType, it) }
        }
    }

    // Find a converter to handle the case where the getter returns an unboxed value from the value class.
    override fun findSerializationConverter(a: Annotated): Converter<*, *>? = (a as? AnnotatedMethod)
        ?.let { _ -> a.findValueClassBoxConverter { it.isUnboxableValueClass() } }

    // Determine if the `unbox` result of `value class` is `nullable
    // @see findNullSerializer
    private fun Class<*>.requireRebox(): Boolean = isUnboxableValueClass() &&
        cache.getKmClass(this)!!.properties.first { it.fieldSignature != null }.returnType.isNullable()

    // Perform proper serialization even if the value wrapped by the value class is null.
    // If value is a non-null object type, it must not be reboxing.
    override fun findNullSerializer(am: Annotated): JsonSerializer<*>? = (am as? AnnotatedMethod)?.let { _ ->
        am.findValueClassBoxConverter { it.requireRebox() }?.let { StdDelegatingSerializer(it) }
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

private fun ValueParameter.createStrictNullChecksConverterOrNull(rawType: Class<*>): Converter<*, *>? {
    @Suppress("UNCHECKED_CAST")
    return when {
        Array::class.java.isAssignableFrom(rawType) && !this.isNullishTypeAt(0) ->
            CollectionValueStrictNullChecksConverter.ForArray(this)
        Iterable::class.java.isAssignableFrom(rawType) && !this.isNullishTypeAt(0) ->
            CollectionValueStrictNullChecksConverter.ForIterable(this)
        Map::class.java.isAssignableFrom(rawType) && !this.isNullishTypeAt(1) ->
            MapValueStrictNullChecksConverter(this)
        else -> null
    }
}
