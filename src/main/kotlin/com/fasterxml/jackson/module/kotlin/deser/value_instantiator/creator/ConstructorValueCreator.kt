package com.fasterxml.jackson.module.kotlin.deser.value_instantiator.creator

import com.fasterxml.jackson.module.kotlin.defaultConstructorMarker
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.argument_bucket.ArgumentBucket
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.argument_bucket.BucketGenerator
import com.fasterxml.jackson.module.kotlin.hasVarargParam
import com.fasterxml.jackson.module.kotlin.toKmClass
import com.fasterxml.jackson.module.kotlin.toSignature
import kotlinx.metadata.KmClass
import kotlinx.metadata.jvm.signature
import java.lang.reflect.Constructor

internal class ConstructorValueCreator<T : Any>(private val constructor: Constructor<T>) : ValueCreator<T>() {
    private val declaringClass: Class<T> = constructor.declaringClass

    override val isAccessible: Boolean = constructor.isAccessible
    override val callableName: String = constructor.name
    override val valueParameters: List<ValueParameter>
    override val bucketGenerator: BucketGenerator

    init {
        // To prevent the call from failing, save the initial value and then rewrite the flag.
        if (!isAccessible) constructor.isAccessible = true

        val declaringKmClass: KmClass = declaringClass.toKmClass()

        val constructorParameters = constructor.toSignature().desc.let { desc ->
            // Compare only desc for performance
            declaringKmClass.constructors.first { desc == it.signature?.desc }
        }.valueParameters

        valueParameters = constructorParameters.map { ValueParameter(it) }
        bucketGenerator = BucketGenerator(constructor.parameterTypes.asList(), constructorParameters.hasVarargParam())
    }

    private val defaultConstructor: Constructor<T> by lazy {
        val maskSize = (constructor.parameters.size + Integer.SIZE - 1) / Integer.SIZE

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
        constructor.newInstance(*args.getArgs())
    } else {
        defaultConstructor.newInstance(*args.getDefaultArgs())
    }
}
