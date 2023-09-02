package io.github.projectmapk.jackson.module.kogera.annotation_introspector

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.cfg.MapperConfig
import com.fasterxml.jackson.databind.introspect.Annotated
import com.fasterxml.jackson.databind.introspect.AnnotatedClass
import com.fasterxml.jackson.databind.introspect.AnnotatedMember
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.databind.util.Converter
import io.github.projectmapk.jackson.module.kogera.KotlinDuration
import io.github.projectmapk.jackson.module.kogera.ReflectionCache
import io.github.projectmapk.jackson.module.kogera.ValueClassUnboxConverter
import io.github.projectmapk.jackson.module.kogera.deser.CollectionValueStrictNullChecksConverter
import io.github.projectmapk.jackson.module.kogera.deser.MapValueStrictNullChecksConverter
import io.github.projectmapk.jackson.module.kogera.isNullable
import io.github.projectmapk.jackson.module.kogera.isUnboxableValueClass
import io.github.projectmapk.jackson.module.kogera.reconstructClassOrNull
import io.github.projectmapk.jackson.module.kogera.ser.KotlinDurationValueToJavaDurationConverter
import io.github.projectmapk.jackson.module.kogera.ser.KotlinToJavaDurationConverter
import io.github.projectmapk.jackson.module.kogera.ser.SequenceToIteratorConverter
import kotlinx.metadata.KmTypeProjection
import kotlinx.metadata.KmValueParameter
import kotlinx.metadata.jvm.fieldSignature
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
    override fun findDeserializationConverter(a: Annotated): Any? = (a as? AnnotatedParameter)?.let { param ->
        findKotlinParameter(param)?.let { valueParameter ->
            val rawType = a.rawType

            valueParameter.createValueClassUnboxConverterOrNull(rawType) ?: run {
                if (strictNullChecks) {
                    valueParameter.createStrictNullChecksConverterOrNull(a.type)
                } else {
                    null
                }
            }
        }
    }

    override fun findSerializationConverter(a: Annotated): Converter<*, *>? = when (a) {
        // Find a converter to handle the case where the getter returns an unboxed value from the value class.
        is AnnotatedMethod -> cache.findValueClassReturnType(a)?.let {
            if (useJavaDurationConversion && it == KotlinDuration::class.java) {
                if (a.rawReturnType == KotlinDuration::class.java) {
                    KotlinToJavaDurationConverter
                } else {
                    KotlinDurationValueToJavaDurationConverter
                }
            } else {
                cache.getValueClassBoxConverter(a.rawReturnType, it)
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
        cache.getJmClass(this)!!.properties.first { it.fieldSignature != null }.returnType.isNullable()

    // Perform proper serialization even if the value wrapped by the value class is null.
    // If value is a non-null object type, it must not be reboxing.
    override fun findNullSerializer(am: Annotated): JsonSerializer<*>? = (am as? AnnotatedMethod)?.let { _ ->
        cache.findValueClassReturnType(am)?.let {
            if (it.requireRebox()) cache.getValueClassBoxConverter(am.rawReturnType, it).delegatingSerializer else null
        }
    }

    /*
     * ClosedRange, which is not a concrete type like IntRange, does not have a type to deserialize to,
     * so deserialization by ClosedRangeMixin does not work.
     * Therefore, this process provides a concrete type.
     *
     * The target of processing is ClosedRange and interfaces or abstract classes that inherit from it.
     * As of Kotlin 1.5.32, ClosedRange and ClosedFloatingPointRange are processed.
     */
    override fun refineDeserializationType(config: MapperConfig<*>, a: Annotated, baseType: JavaType): JavaType {
        return (a as? AnnotatedClass)
            ?.let { _ ->
                a.rawType.apply {
                    if (this != ClosedRange::class.java && this != ClosedFloatingPointRange::class.java) return@let null
                }

                baseType.bindings.typeParameters.firstOrNull()
                    ?.let { ClosedRangeHelpers.findClosedFloatingPointRangeRef(it.rawClass) }
                    ?: ClosedRangeHelpers.comparableRangeClass?.let {
                        val factory = config.typeFactory
                        factory.constructParametricType(it, a.type.bindings)
                    }
            } ?: baseType
    }
}

// At present, it depends on the private class, but if it is made public, it must be switched to a direct reference.
// see https://youtrack.jetbrains.com/issue/KT-55376
internal object ClosedRangeHelpers {
    val closedDoubleRangeRef: JavaType? by lazy {
        runCatching { Class.forName("kotlin.ranges.ClosedDoubleRange") }.getOrNull()
            ?.let { TypeFactory.defaultInstance().constructType(it) }
    }

    val closedFloatRangeRef: JavaType? by lazy {
        runCatching { Class.forName("kotlin.ranges.ClosedFloatRange") }.getOrNull()
            ?.let { TypeFactory.defaultInstance().constructType(it) }
    }

    fun findClosedFloatingPointRangeRef(contentType: Class<*>): JavaType? = when (contentType) {
        Double::class.javaPrimitiveType, Double::class.javaObjectType -> closedDoubleRangeRef
        Float::class.javaPrimitiveType, Float::class.javaObjectType -> closedFloatRangeRef
        else -> null
    }

    val comparableRangeClass: Class<*>? by lazy {
        runCatching { Class.forName("kotlin.ranges.ComparableRange") }.getOrNull()
    }
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

private fun KmValueParameter.createStrictNullChecksConverterOrNull(type: JavaType): Converter<*, *>? {
    return when {
        type.isArrayType && !this.isNullishTypeAt(0) ->
            CollectionValueStrictNullChecksConverter.ForArray(type, this.name)
        type.isCollectionLikeType && !this.isNullishTypeAt(0) ->
            CollectionValueStrictNullChecksConverter.ForIterable(type, this.name)
        type.isMapLikeType && !this.isNullishTypeAt(1) ->
            MapValueStrictNullChecksConverter(type, this.name)
        else -> null
    }
}
