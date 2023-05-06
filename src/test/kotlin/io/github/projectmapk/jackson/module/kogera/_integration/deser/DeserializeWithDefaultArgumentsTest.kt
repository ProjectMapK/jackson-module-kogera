package io.github.projectmapk.jackson.module.kogera._integration.deser

import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DeserializeWithDefaultArgumentsTest {
    data class Dst32(
        val p00: Int = 0,
        val p01: Int = 1,
        val p02: Int = 2,
        val p03: Int = 3,
        val p04: Int = 4,
        val p05: Int = 5,
        val p06: Int = 6,
        val p07: Int = 7,
        val p08: Int = 8,
        val p09: Int = 9,
        val p10: Int = 10,
        val p11: Int = 11,
        val p12: Int = 12,
        val p13: Int = 13,
        val p14: Int = 14,
        val p15: Int = 15,
        val p16: Int = 16,
        val p17: Int = 17,
        val p18: Int = 18,
        val p19: Int = 19,
        val p20: Int = 20,
        val p21: Int = 21,
        val p22: Int = 22,
        val p23: Int = 23,
        val p24: Int = 24,
        val p25: Int = 25,
        val p26: Int = 26,
        val p27: Int = 27,
        val p28: Int = 28,
        val p29: Int = 29,
        val p30: Int = 30,
        val p31: Int = 31
    )

    @Test
    fun test32() {
        val mapper = jacksonObjectMapper()
        assertEquals(Dst32(), mapper.readValue<Dst32>("{}"))
    }

    data class Dst33(
        val p00: Int = 0,
        val p01: Int = 1,
        val p02: Int = 2,
        val p03: Int = 3,
        val p04: Int = 4,
        val p05: Int = 5,
        val p06: Int = 6,
        val p07: Int = 7,
        val p08: Int = 8,
        val p09: Int = 9,
        val p10: Int = 10,
        val p11: Int = 11,
        val p12: Int = 12,
        val p13: Int = 13,
        val p14: Int = 14,
        val p15: Int = 15,
        val p16: Int = 16,
        val p17: Int = 17,
        val p18: Int = 18,
        val p19: Int = 19,
        val p20: Int = 20,
        val p21: Int = 21,
        val p22: Int = 22,
        val p23: Int = 23,
        val p24: Int = 24,
        val p25: Int = 25,
        val p26: Int = 26,
        val p27: Int = 27,
        val p28: Int = 28,
        val p29: Int = 29,
        val p30: Int = 30,
        val p31: Int = 31,
        val p32: Int = 32
    )

    @Test
    fun test33() {
        val mapper = jacksonObjectMapper()
        assertEquals(Dst33(), mapper.readValue<Dst33>("{}"))
    }
}
