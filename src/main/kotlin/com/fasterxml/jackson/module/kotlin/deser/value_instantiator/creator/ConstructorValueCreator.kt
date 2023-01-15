package com.fasterxml.jackson.module.kotlin.deser.value_instantiator.creator

import com.fasterxml.jackson.module.kotlin.defaultConstructorMarker
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.argument_bucket.ArgumentBucket
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.argument_bucket.BucketGenerator
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.calcMaskSize
import com.fasterxml.jackson.module.kotlin.findKmConstructor
import com.fasterxml.jackson.module.kotlin.hasVarargParam
import kotlinx.metadata.KmClass
import java.lang.reflect.Constructor

internal class ConstructorValueCreator<T : Any>(
    private val constructor: Constructor<T>,
    declaringKmClass: KmClass
) : ValueCreator<T>() {
    private val declaringClass: Class<T> = constructor.declaringClass

    override val isAccessible: Boolean = constructor.isAccessible
    override val callableName: String = constructor.name
    override val valueParameters: List<ValueParameter>
    override val bucketGenerator: BucketGenerator

    init {
        // To prevent the call from failing, save the initial value and then rewrite the flag.
        if (!isAccessible) constructor.isAccessible = true

        val constructorParameters = declaringKmClass.findKmConstructor(constructor)!!.valueParameters

        valueParameters = constructorParameters.map { ValueParameter(it) }
        bucketGenerator = BucketGenerator(constructor.parameterTypes.asList(), constructorParameters.hasVarargParam())
    }

    private val defaultConstructor: Constructor<T> by lazy {
        val maskSize = calcMaskSize(constructor.parameters.size)

        val defaultTypes = constructor.parameterTypes.let {
            val parameterSize = it.size
            val temp = it.copyOf(parameterSize + maskSize + 1)
            for (i in 0 until maskSize) {
                temp[it.size + i] = Int::class.javaPrimitiveType
            }
            temp[parameterSize + maskSize] = defaultConstructorMarker

            temp
        }

        declaringClass.getDeclaredConstructor(*defaultTypes).apply {
            if (!this.isAccessible) this.isAccessible = true
        }
    }

    override fun callBy(args: ArgumentBucket): T = if (args.isFullInitialized) {
        SpreadWrapper.newInstance(constructor, args.arguments)
    } else {
        val valueParameterSize = args.valueParameterSize
        val maskSize = args.masks.size

        // +1 for DefaultConstructorMarker
        val defaultArgs = args.arguments.copyOf(valueParameterSize + maskSize + 1)
        for (i in 0 until maskSize) {
            defaultArgs[i + valueParameterSize] = args.masks[i]
        }

        SpreadWrapper.newInstance(defaultConstructor, defaultArgs)
    }
}
