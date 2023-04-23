package com.fasterxml.jackson.module.kotlin._integration

import com.fasterxml.jackson.module.kotlin.annotation_introspector.ClosedRangeHelpers
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class ClosedRangesTest {
    companion object {
        val mapper = jacksonObjectMapper()
    }

    @Test
    fun intLikeRange() {
        val src = IntRange(0, 1)
        val json = mapper.writeValueAsString(src)
        val result = mapper.readValue<IntRange>(json)

        assertEquals(src, result)
    }

    @Test
    fun closedDoubleRange() {
        val src: ClosedFloatingPointRange<Double> = 0.0..1.0
        val json = mapper.writeValueAsString(src)
        val result = mapper.readValue<ClosedRange<Double>>(json)

        assertEquals(src, result)
    }

    @Test
    fun closedFloatRange() {
        val src: ClosedFloatingPointRange<Float> = 0.0f..1.0f
        val json = mapper.writeValueAsString(src)
        val result = mapper.readValue<ClosedFloatingPointRange<Float>>(json)

        assertEquals(src, result)
    }

    private data class Wrapper(val value: Int) : Comparable<Wrapper> {
        override fun compareTo(other: Wrapper): Int = value.compareTo(other.value)
    }

    @Test
    fun comparableRange() {
        val src: ClosedRange<Wrapper> = Wrapper(0)..Wrapper(1)
        val json = mapper.writeValueAsString(src)
        val result = mapper.readValue<ClosedRange<Wrapper>>(json)

        assertEquals(src, result)
    }

    @Test
    fun loadClasses() {
        assertNotNull(ClosedRangeHelpers.closedDoubleRangeRef)
        assertNotNull(ClosedRangeHelpers.closedFloatRangeRef)
        assertNotNull(ClosedRangeHelpers.comparableRangeClass)
    }

    @Test
    fun findClosedFloatingPointRangeRefTest() {
        assertEquals(
            ClosedRangeHelpers.closedDoubleRangeRef,
            ClosedRangeHelpers.findClosedFloatingPointRangeRef(Double::class.javaPrimitiveType!!)
        )
        assertEquals(
            ClosedRangeHelpers.closedDoubleRangeRef,
            ClosedRangeHelpers.findClosedFloatingPointRangeRef(Double::class.javaObjectType)
        )

        assertEquals(
            ClosedRangeHelpers.closedFloatRangeRef,
            ClosedRangeHelpers.findClosedFloatingPointRangeRef(Float::class.javaPrimitiveType!!)
        )
        assertEquals(
            ClosedRangeHelpers.closedFloatRangeRef,
            ClosedRangeHelpers.findClosedFloatingPointRangeRef(Float::class.javaObjectType)
        )
    }
}
