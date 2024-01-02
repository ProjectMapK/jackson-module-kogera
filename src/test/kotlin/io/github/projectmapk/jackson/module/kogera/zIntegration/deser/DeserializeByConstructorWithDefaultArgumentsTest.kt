package io.github.projectmapk.jackson.module.kogera.zIntegration.deser

import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DeserializeByConstructorWithDefaultArgumentsTest {
    data class Dst32(
        val p00: String = "0",
        val p01: String = "1",
        val p02: String = "2",
        val p03: String = "3",
        val p04: String = "4",
        val p05: String = "5",
        val p06: String = "6",
        val p07: String = "7",
        val p08: String = "8",
        val p09: String = "9",
        val p10: String = "10",
        val p11: String = "11",
        val p12: String = "12",
        val p13: String = "13",
        val p14: String = "14",
        val p15: String = "15",
        val p16: String = "16",
        val p17: String = "17",
        val p18: String = "18",
        val p19: String = "19",
        val p20: String = "20",
        val p21: String = "21",
        val p22: String = "22",
        val p23: String = "23",
        val p24: String = "24",
        val p25: String = "25",
        val p26: String = "26",
        val p27: String = "27",
        val p28: String = "28",
        val p29: String = "29",
        val p30: String = "30",
        val p31: String = "31"
    )

    @Test
    fun test32() {
        assertEquals(Dst32(), defaultMapper.readValue<Dst32>("{}"))
    }

    data class Dst33(
        val p00: String = "0",
        val p01: String = "1",
        val p02: String = "2",
        val p03: String = "3",
        val p04: String = "4",
        val p05: String = "5",
        val p06: String = "6",
        val p07: String = "7",
        val p08: String = "8",
        val p09: String = "9",
        val p10: String = "10",
        val p11: String = "11",
        val p12: String = "12",
        val p13: String = "13",
        val p14: String = "14",
        val p15: String = "15",
        val p16: String = "16",
        val p17: String = "17",
        val p18: String = "18",
        val p19: String = "19",
        val p20: String = "20",
        val p21: String = "21",
        val p22: String = "22",
        val p23: String = "23",
        val p24: String = "24",
        val p25: String = "25",
        val p26: String = "26",
        val p27: String = "27",
        val p28: String = "28",
        val p29: String = "29",
        val p30: String = "30",
        val p31: String = "31",
        val p32: String = "32"
    )

    @Test
    fun test33() {
        assertEquals(Dst33(), defaultMapper.readValue<Dst33>("{}"))
    }
}
