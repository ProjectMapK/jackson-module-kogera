package io.github.projectmapk.jackson.module.kogera._integration

import io.github.projectmapk.jackson.module.kogera.ClosedRangeResolver
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
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
        assertNotNull(ClosedRangeResolver.closedDoubleRangeRef)
        assertNotNull(ClosedRangeResolver.closedFloatRangeRef)
        assertNotNull(ClosedRangeResolver.comparableRangeClass)
    }

    @Test
    fun findClosedFloatingPointRangeRefTest() {
        assertEquals(
            ClosedRangeResolver.closedDoubleRangeRef,
            ClosedRangeResolver.findClosedFloatingPointRangeRef(Double::class.javaPrimitiveType!!)
        )
        assertEquals(
            ClosedRangeResolver.closedDoubleRangeRef,
            ClosedRangeResolver.findClosedFloatingPointRangeRef(Double::class.javaObjectType)
        )

        assertEquals(
            ClosedRangeResolver.closedFloatRangeRef,
            ClosedRangeResolver.findClosedFloatingPointRangeRef(Float::class.javaPrimitiveType!!)
        )
        assertEquals(
            ClosedRangeResolver.closedFloatRangeRef,
            ClosedRangeResolver.findClosedFloatingPointRangeRef(Float::class.javaObjectType)
        )
    }
}
