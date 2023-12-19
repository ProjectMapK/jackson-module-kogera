package io.github.projectmapk.jackson.module.kogera.deser.valueInstantiator.creator

import io.github.projectmapk.jackson.module.kogera.JmClass
import io.github.projectmapk.jackson.module.kogera.ReflectionCache
import io.github.projectmapk.jackson.module.kogera.call
import io.github.projectmapk.jackson.module.kogera.defaultConstructorMarker
import io.github.projectmapk.jackson.module.kogera.deser.valueInstantiator.argumentBucket.ArgumentBucket
import io.github.projectmapk.jackson.module.kogera.deser.valueInstantiator.argumentBucket.BucketGenerator
import io.github.projectmapk.jackson.module.kogera.deser.valueInstantiator.calcMaskSize
import io.github.projectmapk.jackson.module.kogera.getDeclaredConstructorBy
import java.lang.reflect.Constructor

internal class ConstructorValueCreator<T : Any>(
    private val constructor: Constructor<T>,
    declaringJmClass: JmClass,
    cache: ReflectionCache
) : ValueCreator<T>() {
    private val declaringClass: Class<T> = constructor.declaringClass

    override val isAccessible: Boolean = constructor.isAccessible
    override val callableName: String = constructor.name
    override val valueParameters: List<ValueParameter>
    override val bucketGenerator: BucketGenerator

    init {
        // To prevent the call from failing, save the initial value and then rewrite the flag.
        if (!isAccessible) constructor.isAccessible = true

        val constructorParameters = declaringJmClass.findKmConstructor(constructor)!!.valueParameters

        valueParameters = constructorParameters.map { ValueParameter(it) }
        val rawTypes = constructor.parameterTypes.asList()
        bucketGenerator = BucketGenerator(
            rawTypes,
            valueParameters,
            constructorParameters.mapToConverters(rawTypes, cache)
        )
    }

    private val defaultConstructor: Constructor<T> by lazy {
        val maskSize = calcMaskSize(constructor.parameters.size)

        @Suppress("UNCHECKED_CAST")
        val defaultTypes = constructor.parameterTypes.let {
            val parameterSize = it.size
            val temp = it.copyOf(parameterSize + maskSize + 1)
            for (i in 0 until maskSize) {
                temp[it.size + i] = Int::class.javaPrimitiveType
            }
            temp[parameterSize + maskSize] = defaultConstructorMarker

            temp
        } as Array<Class<*>>

        declaringClass.getDeclaredConstructorBy(defaultTypes).apply {
            if (!this.isAccessible) this.isAccessible = true
        }
    }

    override fun callBy(args: ArgumentBucket): T = if (args.isFullInitialized) {
        constructor.call(args.arguments)
    } else {
        val valueParameterSize = args.valueParameterSize
        val maskSize = args.masks.size

        // +1 for DefaultConstructorMarker
        val defaultArgs = args.arguments.copyOf(valueParameterSize + maskSize + 1)
        for (i in 0 until maskSize) {
            defaultArgs[i + valueParameterSize] = args.masks[i]
        }

        defaultConstructor.call(defaultArgs)
    }
}
