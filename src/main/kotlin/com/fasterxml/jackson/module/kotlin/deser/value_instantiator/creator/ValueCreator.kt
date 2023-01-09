package com.fasterxml.jackson.module.kotlin.deser.value_instantiator.creator

import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.argument_bucket.ArgumentBucket
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.argument_bucket.BucketGenerator

/**
 * A class that abstracts the creation of instances by calling KFunction.
 * @see com.fasterxml.jackson.module.kotlin.deser.value_instantiator
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

    /**
     * Checking process to see if access from context is possible.
     * @throws IllegalAccessException
     */
    fun checkAccessibility(ctxt: DeserializationContext) {
        if ((!isAccessible && ctxt.config.isEnabled(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS)) ||
            (isAccessible && ctxt.config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS))
        ) {
            return
        }

        throw IllegalAccessException("Cannot access to function, target: $callableName")
    }

    /**
     * Function call with default values enabled.
     */
    abstract fun callBy(args: ArgumentBucket): T
}
