package io.github.projectmapk.jackson.module.kogera.deser.valueInstantiator.creator

import io.github.projectmapk.jackson.module.kogera.JmClass
import io.github.projectmapk.jackson.module.kogera.call
import io.github.projectmapk.jackson.module.kogera.deser.valueInstantiator.argumentBucket.ArgumentBucket
import io.github.projectmapk.jackson.module.kogera.deser.valueInstantiator.argumentBucket.BucketGenerator
import io.github.projectmapk.jackson.module.kogera.deser.valueInstantiator.calcMaskSize
import io.github.projectmapk.jackson.module.kogera.getDeclaredMethodBy
import io.github.projectmapk.jackson.module.kogera.hasVarargParam
import kotlinx.metadata.KmFunction
import java.lang.reflect.Method

internal class MethodValueCreator<T>(private val method: Method, declaringJmClass: JmClass) : ValueCreator<T>() {
    private val companion: JmClass.CompanionObject = declaringJmClass.companion!!
    override val isAccessible: Boolean = method.isAccessible && companion.isAccessible
    override val callableName: String = method.name
    override val valueParameters: List<ValueParameter>
    override val bucketGenerator: BucketGenerator

    init {
        // To prevent the call from failing, save the initial value and then rewrite the flag.
        if (!method.isAccessible) method.isAccessible = true

        val kmFunction: KmFunction = companion.findFunctionByMethod(method)!!
        valueParameters = kmFunction.valueParameters.map { ValueParameter(it) }
        bucketGenerator = BucketGenerator(method.parameterTypes.asList(), kmFunction.valueParameters.hasVarargParam())
    }

    private val defaultCaller: (args: ArgumentBucket) -> Any? by lazy {
        val valueParameterSize = method.parameterTypes.size
        val maskSize = calcMaskSize(valueParameterSize)
        val companionObjectClass = companion.type

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
        val companionObject = companion.instance

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
