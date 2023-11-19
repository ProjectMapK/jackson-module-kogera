package io.github.projectmapk.jackson.module.kogera.annotationIntrospector

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.introspect.Annotated
import com.fasterxml.jackson.databind.introspect.AnnotatedClass
import com.fasterxml.jackson.databind.introspect.AnnotatedMember
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector
import com.fasterxml.jackson.databind.util.Converter
import io.github.projectmapk.jackson.module.kogera.KotlinDuration
import io.github.projectmapk.jackson.module.kogera.ReflectionCache
import io.github.projectmapk.jackson.module.kogera.ValueClassUnboxConverter
import io.github.projectmapk.jackson.module.kogera.annotation.JsonKUnbox
import io.github.projectmapk.jackson.module.kogera.isNullable
import io.github.projectmapk.jackson.module.kogera.isUnboxableValueClass
import io.github.projectmapk.jackson.module.kogera.reconstructClassOrNull
import io.github.projectmapk.jackson.module.kogera.ser.KotlinDurationValueToJavaDurationConverter
import io.github.projectmapk.jackson.module.kogera.ser.KotlinToJavaDurationConverter
import io.github.projectmapk.jackson.module.kogera.ser.SequenceToIteratorConverter
import kotlinx.metadata.KmTypeProjection
import kotlinx.metadata.KmValueParameter
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.lang.reflect.Modifier

// AnnotationIntrospector to be run after default AnnotationIntrospector
// (in most cases, JacksonAnnotationIntrospector).
// Original name: KotlinNamesAnnotationIntrospector
internal class KotlinFallbackAnnotationIntrospector(
    private val strictNullChecks: Boolean,
    private val useJavaDurationConversion: Boolean,
    private val cache: ReflectionCache
) : NopAnnotationIntrospector() {
    private fun findKotlinParameter(param: AnnotatedParameter): KmValueParameter? =
        when (val owner = param.owner.member) {
            is Constructor<*> -> cache.getJmClass(param.declaringClass)?.findKmConstructor(owner)?.valueParameters
            is Method -> if (Modifier.isStatic(owner.modifiers)) {
                cache.getJmClass(param.declaringClass)
                    ?.companion
                    ?.let { it.findFunctionByMethod(owner)?.valueParameters }
            } else {
                null
            }
            else -> null
        }?.let { it[param.index] }

    private fun findKotlinParameter(param: Annotated): KmValueParameter? =
        (param as? AnnotatedParameter)?.let { findKotlinParameter(it) }

    // since 2.4
    override fun findImplicitPropertyName(member: AnnotatedMember): String? = when (member) {
        is AnnotatedMethod -> if (member.parameterCount == 0) {
            cache.getJmClass(member.declaringClass)?.findPropertyByGetter(member.annotated)?.name
        } else {
            null
        }
        is AnnotatedParameter -> findKotlinParameter(member)?.name
        else -> null
    }

    // returns Converter when the argument on Java is an unboxed value class
    override fun findDeserializationConverter(a: Annotated): Any? =
        findKotlinParameter(a)?.createValueClassUnboxConverterOrNull(a.rawType)

    override fun findSerializationConverter(a: Annotated): Converter<*, *>? = when (a) {
        // Find a converter to handle the case where the getter returns an unboxed value from the value class.
        is AnnotatedMethod -> cache.findBoxedReturnType(a.member)?.let {
            if (useJavaDurationConversion && it == KotlinDuration::class.java) {
                if (a.rawReturnType == KotlinDuration::class.java) {
                    KotlinToJavaDurationConverter
                } else {
                    KotlinDurationValueToJavaDurationConverter
                }
            } else {
                // If JsonUnbox is specified, the unboxed getter is used as is.
                if (a.hasAnnotation(JsonKUnbox::class.java) || it.getAnnotation(JsonKUnbox::class.java) != null) {
                    null
                } else {
                    cache.getValueClassBoxConverter(a.rawReturnType, it)
                }
            }
        }
        is AnnotatedClass -> lookupKotlinTypeConverter(a)
        else -> null
    }

    private fun lookupKotlinTypeConverter(a: AnnotatedClass) = when {
        Sequence::class.java.isAssignableFrom(a.rawType) -> SequenceToIteratorConverter(a.type)
        KotlinDuration::class.java == a.rawType -> KotlinToJavaDurationConverter.takeIf { useJavaDurationConversion }
        else -> null
    }

    // Determine if the unbox result of value class is nullable
    // @see findNullSerializer
    private fun Class<*>.requireRebox(): Boolean =
        cache.getJmClass(this)!!.inlineClassUnderlyingType!!.isNullable()

    // Perform proper serialization even if the value wrapped by the value class is null.
    // If value is a non-null object type, it must not be reboxing.
    override fun findNullSerializer(am: Annotated): JsonSerializer<*>? = (am as? AnnotatedMethod)?.let { _ ->
        cache.findBoxedReturnType(am.member)?.let {
            if (it.requireRebox()) cache.getValueClassBoxConverter(am.rawReturnType, it).delegatingSerializer else null
        }
    }

    override fun findSetterInfo(ann: Annotated): JsonSetter.Value = ann.takeIf { strictNullChecks }
        ?.let { _ ->
            findKotlinParameter(ann)?.let { valueParameter ->
                if (valueParameter.requireStrictNullCheck(ann.type)) {
                    JsonSetter.Value.forContentNulls(Nulls.FAIL)
                } else {
                    null
                }
            }
        }
        ?: super.findSetterInfo(ann)
}

private fun KmValueParameter.createValueClassUnboxConverterOrNull(rawType: Class<*>): ValueClassUnboxConverter<*>? {
    return type.reconstructClassOrNull()?.let {
        if (it.isUnboxableValueClass() && it != rawType) ValueClassUnboxConverter(it) else null
    }
}

private fun KmValueParameter.isNullishTypeAt(index: Int): Boolean = type.arguments.getOrNull(index)?.let {
    // If it is not a StarProjection, type is not null
    it === KmTypeProjection.STAR || it.type!!.isNullable()
} ?: true // If a type argument cannot be taken, treat it as nullable to avoid unexpected failure.

private fun KmValueParameter.requireStrictNullCheck(type: JavaType): Boolean =
    ((type.isArrayType || type.isCollectionLikeType) && !this.isNullishTypeAt(0)) ||
        (type.isMapLikeType && !this.isNullishTypeAt(1))
