package com.fasterxml.jackson.module.kotlin.deser.value_instantiator.creator

import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.argument_bucket.ArgumentBucket
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.argument_bucket.BucketGenerator
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.calcMaskSize
import com.fasterxml.jackson.module.kotlin.hasVarargParam
import com.fasterxml.jackson.module.kotlin.toKmClass
import com.fasterxml.jackson.module.kotlin.toSignature
import kotlinx.metadata.KmClass
import kotlinx.metadata.KmFunction
import kotlinx.metadata.jvm.signature
import java.lang.reflect.Field
import java.lang.reflect.Method

internal class MethodValueCreator<T>(private val method: Method, declaringKmClass: KmClass) : ValueCreator<T>() {
    // companion object is always present in the case of a factory function
    private val companionField: Field
    private val companionObjectClass: Class<*>

    override val isAccessible: Boolean
    override val callableName: String = method.name
    override val valueParameters: List<ValueParameter>
    override val bucketGenerator: BucketGenerator

    init {
        val declaringClass = method.declaringClass
        companionField = declaringClass.getDeclaredField(declaringKmClass.companionObject!!)

        // region: about accessibility
        isAccessible = method.isAccessible && companionField.isAccessible

        // To prevent the call from failing, save the initial value and then rewrite the flag.
        if (!method.isAccessible) method.isAccessible = true
        if (!companionField.isAccessible) companionField.isAccessible = true
        // endregion

        // region: read kotlin metadata information
        companionObjectClass = companionField.type
        val companionKmClass: KmClass = companionObjectClass.toKmClass()!!
        val kmFunction: KmFunction = run {
            val signature = method.toSignature()
            companionKmClass.functions.first { signature == it.signature }
        }

        valueParameters = kmFunction.valueParameters.map { ValueParameter(it) }
        bucketGenerator = BucketGenerator(method.parameterTypes.asList(), kmFunction.valueParameters.hasVarargParam())
        // endregion
    }

    private val defaultCaller: (args: ArgumentBucket) -> Any? by lazy {
        val valueParameterSize = method.parameterTypes.size
        val maskSize = calcMaskSize(valueParameterSize)
        val defaultTypes = method.parameterTypes.let { parameterTypes ->
            // companion object instance(1) + parameterSize + maskSize + marker(1)
            val temp = arrayOfNulls<Class<*>>(1 + valueParameterSize + maskSize + 1)
            temp[0] = companionObjectClass // companion object
            parameterTypes.copyInto(temp, 1) // parameter types
            for (i in (valueParameterSize + 1)..(valueParameterSize + maskSize)) { // masks
                temp[i] = Int::class.javaPrimitiveType
            }
            temp[valueParameterSize + maskSize + 1] = Object::class.java // maker
            temp
        }
        val defaultMethod = companionObjectClass.getDeclaredMethod("${callableName}\$default", *defaultTypes)

        val companionObject = companionField.get(null)

        return@lazy {
            val defaultArgs = arrayOfNulls<Any?>(defaultTypes.size)
            defaultArgs[0] = companionObject
            it.arguments.copyInto(defaultArgs, 1)
            for (i in 0 until maskSize) {
                defaultArgs[i + valueParameterSize + 1] = it.masks[i]
            }

            SpreadWrapper.invoke(defaultMethod, null, defaultArgs)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun callBy(args: ArgumentBucket): T = if (args.isFullInitialized) {
        // It calls static method for simplicity, and is a little slower in terms of speed.
        SpreadWrapper.invoke(method, null, args.arguments)
    } else {
        defaultCaller(args)
    } as T
}
