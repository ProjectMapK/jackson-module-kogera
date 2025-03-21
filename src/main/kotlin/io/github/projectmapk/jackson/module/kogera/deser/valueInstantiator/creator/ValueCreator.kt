package io.github.projectmapk.jackson.module.kogera.deser.valueInstantiator.creator

import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.MapperFeature
import io.github.projectmapk.jackson.module.kogera.ReflectionCache
import io.github.projectmapk.jackson.module.kogera.ValueClassUnboxConverter
import io.github.projectmapk.jackson.module.kogera.deser.valueInstantiator.argumentBucket.ArgumentBucket
import io.github.projectmapk.jackson.module.kogera.deser.valueInstantiator.argumentBucket.BucketGenerator
import io.github.projectmapk.jackson.module.kogera.isUnboxableValueClass
import io.github.projectmapk.jackson.module.kogera.jmClass.JmValueParameter

/**
 * A class that abstracts the creation of instances by calling KFunction.
 * @see io.github.projectmapk.jackson.module.kogera.deser.valueInstantiator
 */
internal sealed class ValueCreator<T> {
    /**
     * Initial value for accessibility by reflection.
     */
    protected abstract val isAccessible: Boolean

    /**
     * Function name for error output
     */
    protected abstract val callableName: String

    /**
     * ValueParameters of the KFunction to be called.
     */
    abstract val valueParameters: List<JmValueParameter>

    protected abstract val bucketGenerator: BucketGenerator

    fun generateBucket(): ArgumentBucket = bucketGenerator.generate()

    private val accessibilityChecker: (DeserializationContext) -> Boolean by lazy {
        return@lazy if (isAccessible) {
            { ctxt -> ctxt.config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS) }
        } else {
            { ctxt -> ctxt.config.isEnabled(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS) }
        }
    }

    /**
     * Checking process to see if access from context is possible.
     * @throws IllegalAccessException
     */
    fun checkAccessibility(ctxt: DeserializationContext) {
        if (accessibilityChecker(ctxt)) {
            return
        }

        throw IllegalAccessException("Cannot access to function, target: $callableName")
    }

    /**
     * Function call with default values enabled.
     */
    abstract fun callBy(args: ArgumentBucket): T
}

@Suppress("UNCHECKED_CAST")
internal fun List<JmValueParameter>.mapToConverters(
    rawTypes: List<Class<*>>,
    cache: ReflectionCache
): List<ValueClassUnboxConverter<Any>?> = mapIndexed { i, param ->
    param.reconstructedClassOrNull
        ?.takeIf { it.isUnboxableValueClass() && rawTypes[i] != it }
        ?.let { cache.getValueClassUnboxConverter(it) }
} as List<ValueClassUnboxConverter<Any>?> // Cast to cheat generics
