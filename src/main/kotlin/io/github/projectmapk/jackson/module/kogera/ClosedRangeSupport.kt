package io.github.projectmapk.jackson.module.kogera

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver

internal abstract class ClosedRangeMixin<T> @JsonCreator constructor(
    public val start: T,
    @get:JsonProperty("end") public val endInclusive: T
) {
    @JsonIgnore
    public abstract fun getEnd(): T

    @JsonIgnore
    public abstract fun getFirst(): T

    @JsonIgnore
    public abstract fun getLast(): T

    @JsonIgnore
    public abstract fun getIncrement(): T

    @JsonIgnore
    public abstract fun isEmpty(): Boolean

    @JsonIgnore
    public abstract fun getStep(): T

    @JsonIgnore
    public abstract fun getEndExclusive(): T
}

/*
 * ClosedRange, which is not a concrete type like IntRange, does not have a type to deserialize to,
 * so deserialization by ClosedRangeMixin does not work.
 * Therefore, this process provides a concrete type.
 *
 * The target of processing is ClosedRange and interfaces or abstract classes that inherit from it.
 * As of Kotlin 1.5.32, ClosedRange and ClosedFloatingPointRange are processed.
 */
internal object ClosedRangeResolver : SimpleAbstractTypeResolver() {
    private fun readResolve(): Any = ClosedRangeResolver

    // At present, it depends on the private class, but if it is made public, it must be switched to a direct reference.
    // see https://youtrack.jetbrains.com/issue/KT-55376
    val closedDoubleRangeRef: Class<*> by lazy {
        Class.forName("kotlin.ranges.ClosedDoubleRange")
    }

    val closedFloatRangeRef: Class<*>? by lazy {
        Class.forName("kotlin.ranges.ClosedFloatRange")
    }

    fun findClosedFloatingPointRangeRef(contentType: Class<*>): Class<*>? = when (contentType) {
        Double::class.javaPrimitiveType, Double::class.javaObjectType -> closedDoubleRangeRef
        Float::class.javaPrimitiveType, Float::class.javaObjectType -> closedFloatRangeRef
        else -> null
    }

    val comparableRangeClass: Class<*> by lazy {
        Class.forName("kotlin.ranges.ComparableRange")
    }

    override fun findTypeMapping(config: DeserializationConfig, type: JavaType): JavaType? {
        val rawClass = type.rawClass

        return if (rawClass == ClosedRange::class.java || rawClass == ClosedFloatingPointRange::class.java) {
            type.bindings.typeParameters.firstOrNull()
                ?.let { typeParam ->
                    findClosedFloatingPointRangeRef(typeParam.rawClass)?.let {
                        config.typeFactory.constructType(it)
                    }
                }
                ?: config.typeFactory.constructParametricType(comparableRangeClass, type.bindings)
        } else {
            super.findTypeMapping(config, type)
        }
    }
}
