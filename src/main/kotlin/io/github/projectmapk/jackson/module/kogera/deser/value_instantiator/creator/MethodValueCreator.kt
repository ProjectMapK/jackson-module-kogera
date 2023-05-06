package io.github.projectmapk.jackson.module.kogera.deser.value_instantiator.creator

import io.github.projectmapk.jackson.module.kogera.call
import io.github.projectmapk.jackson.module.kogera.deser.value_instantiator.argument_bucket.ArgumentBucket
import io.github.projectmapk.jackson.module.kogera.deser.value_instantiator.argument_bucket.BucketGenerator
import io.github.projectmapk.jackson.module.kogera.deser.value_instantiator.calcMaskSize
import io.github.projectmapk.jackson.module.kogera.getDeclaredMethodBy
import io.github.projectmapk.jackson.module.kogera.hasVarargParam
import io.github.projectmapk.jackson.module.kogera.toKmClass
import io.github.projectmapk.jackson.module.kogera.toSignature
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

        @Suppress("UNCHECKED_CAST")
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
        } as Array<Class<*>>

        val defaultMethod = companionObjectClass.getDeclaredMethodBy("${callableName}\$default", defaultTypes)
        val companionObject = companionField.get(null)

        return@lazy {
            val defaultArgs = arrayOfNulls<Any?>(defaultTypes.size)
            defaultArgs[0] = companionObject
            it.arguments.copyInto(defaultArgs, 1)
            for (i in 0 until maskSize) {
                defaultArgs[i + valueParameterSize + 1] = it.masks[i]
            }

            defaultMethod.call(null, defaultArgs)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun callBy(args: ArgumentBucket): T = if (args.isFullInitialized) {
        // It calls static method for simplicity, and is a little slower in terms of speed.
        method.call(null, args.arguments)
    } else {
        defaultCaller(args)
    } as T
}
