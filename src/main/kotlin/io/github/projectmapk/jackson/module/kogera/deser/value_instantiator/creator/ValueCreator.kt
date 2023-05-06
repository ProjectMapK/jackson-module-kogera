package io.github.projectmapk.jackson.module.kogera.deser.value_instantiator.creator

import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.MapperFeature
import io.github.projectmapk.jackson.module.kogera.deser.value_instantiator.argument_bucket.ArgumentBucket
import io.github.projectmapk.jackson.module.kogera.deser.value_instantiator.argument_bucket.BucketGenerator

/**
 * A class that abstracts the creation of instances by calling KFunction.
 * @see io.github.projectmapk.jackson.module.kogera.deser.value_instantiator
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
    abstract val valueParameters: List<ValueParameter>

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
