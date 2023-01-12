package com.fasterxml.jackson.module.kotlin.deser.value_instantiator.argument_bucket

import java.lang.reflect.Array as ReflectArray

private fun defaultPrimitiveValue(type: Class<*>): Any = when (type) {
    Boolean::class.java -> false
    Char::class.java -> 0.toChar()
    Byte::class.java -> 0.toByte()
    Short::class.java -> 0.toShort()
    Int::class.java -> 0
    Float::class.java -> 0f
    Long::class.java -> 0L
    Double::class.java -> 0.0
    Void.TYPE -> throw IllegalStateException("Parameter with void type is illegal")
    else -> throw UnsupportedOperationException("Unknown primitive: $type")
}

private fun defaultEmptyArray(arrayType: Class<*>): Any =
    ReflectArray.newInstance(arrayType.componentType, 0)

// @see https://github.com/JetBrains/kotlin/blob/4c925d05883a8073e6732bca95bf575beb031a59/core/reflection.jvm/src/kotlin/reflect/jvm/internal/KCallableImpl.kt#L114
internal class BucketGenerator(
    parameterTypes: List<Class<*>>,
    hasVarargParam: Boolean
) {
    private val valueParameterSize: Int = parameterTypes.size
    private val originalAbsentArgs: Array<Any?> = Array(valueParameterSize) { i ->
        // Set values of primitive arguments to the boxed default values (such as 0, 0.0, false) instead of nulls.
        parameterTypes[i].takeIf { it.isPrimitive }?.let { defaultPrimitiveValue(it) }
    }
    private val originalMasks: IntArray = IntArray((parameterTypes.size + Integer.SIZE - 1) / Integer.SIZE) { 0xFFFF }

    init {
        if (hasVarargParam) {
            // vararg argument is always at the end of the arguments.
            val i = valueParameterSize - 1
            originalAbsentArgs[i] = defaultEmptyArray(parameterTypes[i])
        }
    }

    fun generate(): ArgumentBucket =
        ArgumentBucket(valueParameterSize, originalAbsentArgs.clone(), originalMasks.clone())
}

internal class ArgumentBucket(
    val valueParameterSize: Int,
    val arguments: Array<Any?>,
    val masks: IntArray
) {
    companion object {
        // List of Int with only 1 bit enabled.
        private val BIT_FLAGS: List<Int> = IntArray(Int.SIZE_BITS) { (1 shl it).inv() }.asList()
    }

    private var count = 0

    operator fun set(index: Int, arg: Any?) {
        // Since there is no multiple initialization in the use case, the key check is omitted.
        arguments[index] = arg

        val maskIndex = index / Integer.SIZE
        masks[maskIndex] = masks[maskIndex] and BIT_FLAGS[index % Integer.SIZE]

        count++
    }

    val isFullInitialized: Boolean get() = count == valueParameterSize
}
