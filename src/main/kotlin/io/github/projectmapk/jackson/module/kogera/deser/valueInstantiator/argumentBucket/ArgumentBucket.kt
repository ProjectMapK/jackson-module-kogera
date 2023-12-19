package io.github.projectmapk.jackson.module.kogera.deser.valueInstantiator.argumentBucket

import io.github.projectmapk.jackson.module.kogera.ValueClassUnboxConverter
import io.github.projectmapk.jackson.module.kogera.deser.valueInstantiator.calcMaskSize
import io.github.projectmapk.jackson.module.kogera.deser.valueInstantiator.creator.ValueParameter
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

// List of Int with only 1 bit enabled.
private val BIT_FLAGS: List<Int> = IntArray(Int.SIZE_BITS) { (1 shl it).inv() }.asList()

private enum class MaskOperation {
    // Set argument.
    SET {
        override fun invoke(i: Int, j: Int) = i and j
    },

    // Mark the argument as uninitialized.
    // A vararg argument that has no default value need not be given and is therefore marked as initialized.
    INIT {
        override fun invoke(i: Int, j: Int): Int = i or j.inv()
    };

    abstract operator fun invoke(i: Int, j: Int): Int
}

private fun IntArray.update(index: Int, operation: MaskOperation) {
    val maskIndex = index / Integer.SIZE
    this[maskIndex] = operation(this[maskIndex], BIT_FLAGS[index % Integer.SIZE])
}

// @see https://github.com/JetBrains/kotlin/blob/4c925d05883a8073e6732bca95bf575beb031a59/core/reflection.jvm/src/kotlin/reflect/jvm/internal/KCallableImpl.kt#L114
internal class BucketGenerator(
    parameterTypes: List<Class<*>>,
    valueParameters: List<ValueParameter>,
    private val converters: List<ValueClassUnboxConverter<Any>?>
) {
    private val valueParameterSize: Int = parameterTypes.size
    private val originalAbsentArgs: Array<Any?>

    // -1 is the filled bit mask.
    private val originalMasks: IntArray = IntArray(calcMaskSize(parameterTypes.size)) { 0 }

    init {
        originalAbsentArgs = Array(valueParameterSize) { i ->
            val paramType = parameterTypes[i]
            val metaParam = valueParameters[i]

            when {
                // In Kotlin, it is possible to define non-tail arguments with vararg,
                // which cannot be determined by Java reflection, so they are read from Metadata.
                metaParam.isVararg -> {
                    // If no default arguments are set,
                    // the call may be made with an empty array even if no arguments are passed,
                    // so it is treated as initialized.
                    // Conversely, if default arguments are set,
                    // the initial value is treated as uninitialized to detect that no arguments has passed.
                    if (metaParam.isOptional) originalMasks.update(i, MaskOperation.INIT)
                    defaultEmptyArray(paramType)
                }
                // Set values of primitive arguments to the boxed default values (such as 0, 0.0, false) instead of nulls.
                paramType.isPrimitive -> {
                    originalMasks.update(i, MaskOperation.INIT)
                    defaultPrimitiveValue(paramType)
                }
                else -> {
                    originalMasks.update(i, MaskOperation.INIT)
                    null
                }
            }
        }
    }

    fun generate(): ArgumentBucket =
        ArgumentBucket(valueParameterSize, originalAbsentArgs.clone(), originalMasks.clone(), converters)
}

internal class ArgumentBucket(
    val valueParameterSize: Int,
    val arguments: Array<Any?>,
    val masks: IntArray,
    private val converters: List<ValueClassUnboxConverter<Any>?>
) {
    /**
     * Sets the argument corresponding to index.
     * Note that, arguments defined in the value class must be passed as boxed.
     */
    operator fun set(index: Int, arg: Any?) {
        val actualArg = arg?.let {
            // unbox by converter
            val converter = converters[index] ?: return@let arg
            converter.convert(arg)
        }

        // Since there is no multiple initialization in the use case, the key check is omitted.
        arguments[index] = actualArg

        val maskIndex = index / Integer.SIZE
        masks[maskIndex] = masks[maskIndex] and BIT_FLAGS[index % Integer.SIZE]

        masks.update(index, MaskOperation.SET)
    }

    val isFullInitialized: Boolean get() = masks.all { it == 0 }
}
