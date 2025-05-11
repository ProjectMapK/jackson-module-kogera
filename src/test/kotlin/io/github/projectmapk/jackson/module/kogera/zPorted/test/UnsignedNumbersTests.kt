package io.github.projectmapk.jackson.module.kogera.zPorted.test

import com.fasterxml.jackson.core.exc.InputCoercionException
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigInteger

internal class UnsignedNumbersTests {
    @Test
    fun `test UByte`() {
        val json = defaultMapper.writeValueAsString(UByte.MAX_VALUE)
        val deserialized = defaultMapper.readValue<UByte>(json)
        assertEquals(UByte.MAX_VALUE, deserialized)
    }

    @Test
    fun `test UByte overflow`() {
        val json = defaultMapper.writeValueAsString(UByte.MAX_VALUE + 1u)
        assertThrows<InputCoercionException> { defaultMapper.readValue<UByte>(json) }
    }

    @Test
    fun `test UByte underflow`() {
        val json = defaultMapper.writeValueAsString(-1)
        assertThrows<InputCoercionException> { defaultMapper.readValue<UByte>(json) }
    }

    @Test
    fun `test UShort`() {
        val json = defaultMapper.writeValueAsString(UShort.MAX_VALUE)
        val deserialized = defaultMapper.readValue<UShort>(json)
        assertEquals(UShort.MAX_VALUE, deserialized)
    }

    @Test
    fun `test UShort overflow`() {
        val json = defaultMapper.writeValueAsString(UShort.MAX_VALUE + 1u)
        assertThrows<InputCoercionException> { defaultMapper.readValue<UShort>(json) }
    }

    @Test
    fun `test UShort underflow`() {
        val json = defaultMapper.writeValueAsString(-1)
        assertThrows<InputCoercionException> { defaultMapper.readValue<UShort>(json) }
    }

    @Test
    fun `test UInt`() {
        val json = defaultMapper.writeValueAsString(UInt.MAX_VALUE)
        val deserialized = defaultMapper.readValue<UInt>(json)
        assertEquals(UInt.MAX_VALUE, deserialized)
    }

    @Test
    fun `test UInt overflow`() {
        val json = defaultMapper.writeValueAsString(UInt.MAX_VALUE.toULong() + 1u)
        assertThrows<InputCoercionException> { defaultMapper.readValue<UInt>(json) }
    }

    @Test
    fun `test UInt underflow`() {
        val json = defaultMapper.writeValueAsString(-1)
        assertThrows<InputCoercionException> { defaultMapper.readValue<UInt>(json) }
    }

    @Test
    fun `test ULong`() {
        val json = defaultMapper.writeValueAsString(ULong.MAX_VALUE)
        val deserialized = defaultMapper.readValue<ULong>(json)
        assertEquals(ULong.MAX_VALUE, deserialized)
    }

    @Test
    fun `test ULong overflow`() {
        val value = BigInteger(ULong.MAX_VALUE.toString()) + BigInteger.ONE
        val json = defaultMapper.writeValueAsString(value)
        assertThrows<InputCoercionException> { defaultMapper.readValue<ULong>(json) }
    }

    @Test
    fun `test ULong underflow`() {
        val json = defaultMapper.writeValueAsString(-1)
        assertThrows<InputCoercionException> { defaultMapper.readValue<ULong>(json) }
    }
}
