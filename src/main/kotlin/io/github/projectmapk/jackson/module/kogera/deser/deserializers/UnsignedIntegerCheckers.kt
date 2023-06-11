package io.github.projectmapk.jackson.module.kogera.deser.deserializers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.exc.InputCoercionException
import java.math.BigInteger

internal sealed class UnsignedIntegerChecker<T : Comparable<T>, U : Comparable<U>> {
    abstract val clazz: Class<T>
    abstract val max: U
    abstract val min: U

    abstract fun U.convert(): T

    fun readWithRangeCheck(p: JsonParser?, value: U): T = value.takeIf { it in min..max }
        ?.convert()
        ?: throw InputCoercionException(
            p,
            "Numeric value ($value) out of range of ${clazz.simpleName} ($min - $max).",
            JsonToken.VALUE_NUMBER_INT,
            clazz
        )
}

// Not Short because it offers little performance benefit and rather increases the risk of overflow.
internal object UByteChecker : UnsignedIntegerChecker<UByte, Int>() {
    override val clazz: Class<UByte> = UByte::class.java
    override val max: Int = UByte.MAX_VALUE.toInt()
    override val min: Int = UByte.MIN_VALUE.toInt()

    override fun Int.convert(): UByte = toUByte()
}

internal object UShortChecker : UnsignedIntegerChecker<UShort, Int>() {
    override val clazz: Class<UShort> = UShort::class.java
    override val max: Int = UShort.MAX_VALUE.toInt()
    override val min: Int = UShort.MIN_VALUE.toInt()

    override fun Int.convert(): UShort = toUShort()
}

internal object UIntChecker : UnsignedIntegerChecker<UInt, Long>() {
    override val clazz: Class<UInt> = UInt::class.java
    override val max: Long = UInt.MAX_VALUE.toLong()
    override val min: Long = UInt.MIN_VALUE.toLong()

    override fun Long.convert(): UInt = toUInt()
}

internal object ULongChecker : UnsignedIntegerChecker<ULong, BigInteger>() {
    override val clazz: Class<ULong> = ULong::class.java
    override val max: BigInteger = BigInteger(ULong.MAX_VALUE.toString())
    override val min: BigInteger = BigInteger.ZERO

    // Since BigInteger.toLong returns a negative value on overflow,
    // it can be converted directly to ULong after range check.
    override fun BigInteger.convert(): ULong = toLong().toULong()
}
