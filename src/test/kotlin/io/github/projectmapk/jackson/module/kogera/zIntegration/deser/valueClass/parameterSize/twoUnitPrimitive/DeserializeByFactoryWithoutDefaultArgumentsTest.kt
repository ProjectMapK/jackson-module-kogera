package io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.parameterSize.twoUnitPrimitive

import com.fasterxml.jackson.annotation.JsonCreator
import io.github.projectmapk.jackson.module.kogera.assertReflectEquals
import io.github.projectmapk.jackson.module.kogera.callPrimaryConstructor
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.TwoUnitPrimitive
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

// Convert the property p to q (but not the value) to make it an input to the factory function.
private fun replacePQ(src: String) = src.replace(Regex("""p\d+":""")) { "q" + it.value.substring(1) }

/**
 * Up to argument size 32 there is one mask argument for the default argument,
 * 33 ~ 64 there are two, and 65 there are three, so each boundary value is tested.
 * Also, the maximum size of arguments that can be set by a constructor
 * that includes a value class as an argument is 126 (one less by DefaultConstructorMarker), so test that case as well.
 */
class DeserializeByFactoryWithoutDefaultArgumentsTest {
    data class Dst32(
        val p00: TwoUnitPrimitive,
        val p01: TwoUnitPrimitive,
        val p02: TwoUnitPrimitive,
        val p03: TwoUnitPrimitive,
        val p04: TwoUnitPrimitive,
        val p05: TwoUnitPrimitive,
        val p06: TwoUnitPrimitive,
        val p07: TwoUnitPrimitive,
        val p08: TwoUnitPrimitive,
        val p09: TwoUnitPrimitive,
        val p10: TwoUnitPrimitive,
        val p11: TwoUnitPrimitive,
        val p12: TwoUnitPrimitive,
        val p13: TwoUnitPrimitive,
        val p14: TwoUnitPrimitive,
        val p15: TwoUnitPrimitive,
        val p16: TwoUnitPrimitive,
        val p17: TwoUnitPrimitive,
        val p18: TwoUnitPrimitive,
        val p19: TwoUnitPrimitive,
        val p20: TwoUnitPrimitive,
        val p21: TwoUnitPrimitive,
        val p22: TwoUnitPrimitive,
        val p23: TwoUnitPrimitive,
        val p24: TwoUnitPrimitive,
        val p25: TwoUnitPrimitive,
        val p26: TwoUnitPrimitive,
        val p27: TwoUnitPrimitive,
        val p28: TwoUnitPrimitive,
        val p29: TwoUnitPrimitive,
        val p30: TwoUnitPrimitive,
        val p31: TwoUnitPrimitive,
    ) {
        companion object {
            @JvmStatic
            @JsonCreator
            fun creator(
                q00: TwoUnitPrimitive,
                q01: TwoUnitPrimitive,
                q02: TwoUnitPrimitive,
                q03: TwoUnitPrimitive,
                q04: TwoUnitPrimitive,
                q05: TwoUnitPrimitive,
                q06: TwoUnitPrimitive,
                q07: TwoUnitPrimitive,
                q08: TwoUnitPrimitive,
                q09: TwoUnitPrimitive,
                q10: TwoUnitPrimitive,
                q11: TwoUnitPrimitive,
                q12: TwoUnitPrimitive,
                q13: TwoUnitPrimitive,
                q14: TwoUnitPrimitive,
                q15: TwoUnitPrimitive,
                q16: TwoUnitPrimitive,
                q17: TwoUnitPrimitive,
                q18: TwoUnitPrimitive,
                q19: TwoUnitPrimitive,
                q20: TwoUnitPrimitive,
                q21: TwoUnitPrimitive,
                q22: TwoUnitPrimitive,
                q23: TwoUnitPrimitive,
                q24: TwoUnitPrimitive,
                q25: TwoUnitPrimitive,
                q26: TwoUnitPrimitive,
                q27: TwoUnitPrimitive,
                q28: TwoUnitPrimitive,
                q29: TwoUnitPrimitive,
                q30: TwoUnitPrimitive,
                q31: TwoUnitPrimitive,
            ) = Dst32(
                q00,
                q01,
                q02,
                q03,
                q04,
                q05,
                q06,
                q07,
                q08,
                q09,
                q10,
                q11,
                q12,
                q13,
                q14,
                q15,
                q16,
                q17,
                q18,
                q19,
                q20,
                q21,
                q22,
                q23,
                q24,
                q25,
                q26,
                q27,
                q28,
                q29,
                q30,
                q31,
            )
        }
    }

    @Test
    fun test32() {
        val expected = callPrimaryConstructor<Dst32> { TwoUnitPrimitive(it.index.toLong()) }
        val src = replacePQ(defaultMapper.writeValueAsString(expected))
        Assertions.assertEquals(expected, defaultMapper.readValue<Dst32>(src))
    }

    data class Dst33(
        val p00: TwoUnitPrimitive,
        val p01: TwoUnitPrimitive,
        val p02: TwoUnitPrimitive,
        val p03: TwoUnitPrimitive,
        val p04: TwoUnitPrimitive,
        val p05: TwoUnitPrimitive,
        val p06: TwoUnitPrimitive,
        val p07: TwoUnitPrimitive,
        val p08: TwoUnitPrimitive,
        val p09: TwoUnitPrimitive,
        val p10: TwoUnitPrimitive,
        val p11: TwoUnitPrimitive,
        val p12: TwoUnitPrimitive,
        val p13: TwoUnitPrimitive,
        val p14: TwoUnitPrimitive,
        val p15: TwoUnitPrimitive,
        val p16: TwoUnitPrimitive,
        val p17: TwoUnitPrimitive,
        val p18: TwoUnitPrimitive,
        val p19: TwoUnitPrimitive,
        val p20: TwoUnitPrimitive,
        val p21: TwoUnitPrimitive,
        val p22: TwoUnitPrimitive,
        val p23: TwoUnitPrimitive,
        val p24: TwoUnitPrimitive,
        val p25: TwoUnitPrimitive,
        val p26: TwoUnitPrimitive,
        val p27: TwoUnitPrimitive,
        val p28: TwoUnitPrimitive,
        val p29: TwoUnitPrimitive,
        val p30: TwoUnitPrimitive,
        val p31: TwoUnitPrimitive,
        val p32: TwoUnitPrimitive,
    ) {
        companion object {
            @JvmStatic
            @JsonCreator
            fun creator(
                q00: TwoUnitPrimitive,
                q01: TwoUnitPrimitive,
                q02: TwoUnitPrimitive,
                q03: TwoUnitPrimitive,
                q04: TwoUnitPrimitive,
                q05: TwoUnitPrimitive,
                q06: TwoUnitPrimitive,
                q07: TwoUnitPrimitive,
                q08: TwoUnitPrimitive,
                q09: TwoUnitPrimitive,
                q10: TwoUnitPrimitive,
                q11: TwoUnitPrimitive,
                q12: TwoUnitPrimitive,
                q13: TwoUnitPrimitive,
                q14: TwoUnitPrimitive,
                q15: TwoUnitPrimitive,
                q16: TwoUnitPrimitive,
                q17: TwoUnitPrimitive,
                q18: TwoUnitPrimitive,
                q19: TwoUnitPrimitive,
                q20: TwoUnitPrimitive,
                q21: TwoUnitPrimitive,
                q22: TwoUnitPrimitive,
                q23: TwoUnitPrimitive,
                q24: TwoUnitPrimitive,
                q25: TwoUnitPrimitive,
                q26: TwoUnitPrimitive,
                q27: TwoUnitPrimitive,
                q28: TwoUnitPrimitive,
                q29: TwoUnitPrimitive,
                q30: TwoUnitPrimitive,
                q31: TwoUnitPrimitive,
                q32: TwoUnitPrimitive,
            ) = Dst33(
                q00,
                q01,
                q02,
                q03,
                q04,
                q05,
                q06,
                q07,
                q08,
                q09,
                q10,
                q11,
                q12,
                q13,
                q14,
                q15,
                q16,
                q17,
                q18,
                q19,
                q20,
                q21,
                q22,
                q23,
                q24,
                q25,
                q26,
                q27,
                q28,
                q29,
                q30,
                q31,
                q32,
            )
        }
    }

    @Test
    fun test33() {
        val expected = callPrimaryConstructor<Dst33> { TwoUnitPrimitive(it.index.toLong()) }
        val src = replacePQ(defaultMapper.writeValueAsString(expected))
        Assertions.assertEquals(expected, defaultMapper.readValue<Dst33>(src))
    }

    data class Dst64(
        val p00: TwoUnitPrimitive,
        val p01: TwoUnitPrimitive,
        val p02: TwoUnitPrimitive,
        val p03: TwoUnitPrimitive,
        val p04: TwoUnitPrimitive,
        val p05: TwoUnitPrimitive,
        val p06: TwoUnitPrimitive,
        val p07: TwoUnitPrimitive,
        val p08: TwoUnitPrimitive,
        val p09: TwoUnitPrimitive,
        val p10: TwoUnitPrimitive,
        val p11: TwoUnitPrimitive,
        val p12: TwoUnitPrimitive,
        val p13: TwoUnitPrimitive,
        val p14: TwoUnitPrimitive,
        val p15: TwoUnitPrimitive,
        val p16: TwoUnitPrimitive,
        val p17: TwoUnitPrimitive,
        val p18: TwoUnitPrimitive,
        val p19: TwoUnitPrimitive,
        val p20: TwoUnitPrimitive,
        val p21: TwoUnitPrimitive,
        val p22: TwoUnitPrimitive,
        val p23: TwoUnitPrimitive,
        val p24: TwoUnitPrimitive,
        val p25: TwoUnitPrimitive,
        val p26: TwoUnitPrimitive,
        val p27: TwoUnitPrimitive,
        val p28: TwoUnitPrimitive,
        val p29: TwoUnitPrimitive,
        val p30: TwoUnitPrimitive,
        val p31: TwoUnitPrimitive,
        val p32: TwoUnitPrimitive,
        val p33: TwoUnitPrimitive,
        val p34: TwoUnitPrimitive,
        val p35: TwoUnitPrimitive,
        val p36: TwoUnitPrimitive,
        val p37: TwoUnitPrimitive,
        val p38: TwoUnitPrimitive,
        val p39: TwoUnitPrimitive,
        val p40: TwoUnitPrimitive,
        val p41: TwoUnitPrimitive,
        val p42: TwoUnitPrimitive,
        val p43: TwoUnitPrimitive,
        val p44: TwoUnitPrimitive,
        val p45: TwoUnitPrimitive,
        val p46: TwoUnitPrimitive,
        val p47: TwoUnitPrimitive,
        val p48: TwoUnitPrimitive,
        val p49: TwoUnitPrimitive,
        val p50: TwoUnitPrimitive,
        val p51: TwoUnitPrimitive,
        val p52: TwoUnitPrimitive,
        val p53: TwoUnitPrimitive,
        val p54: TwoUnitPrimitive,
        val p55: TwoUnitPrimitive,
        val p56: TwoUnitPrimitive,
        val p57: TwoUnitPrimitive,
        val p58: TwoUnitPrimitive,
        val p59: TwoUnitPrimitive,
        val p60: TwoUnitPrimitive,
        val p61: TwoUnitPrimitive,
        val p62: TwoUnitPrimitive,
        val p63: TwoUnitPrimitive,
    ) {
        companion object {
            @JvmStatic
            @JsonCreator
            fun creator(
                q00: TwoUnitPrimitive,
                q01: TwoUnitPrimitive,
                q02: TwoUnitPrimitive,
                q03: TwoUnitPrimitive,
                q04: TwoUnitPrimitive,
                q05: TwoUnitPrimitive,
                q06: TwoUnitPrimitive,
                q07: TwoUnitPrimitive,
                q08: TwoUnitPrimitive,
                q09: TwoUnitPrimitive,
                q10: TwoUnitPrimitive,
                q11: TwoUnitPrimitive,
                q12: TwoUnitPrimitive,
                q13: TwoUnitPrimitive,
                q14: TwoUnitPrimitive,
                q15: TwoUnitPrimitive,
                q16: TwoUnitPrimitive,
                q17: TwoUnitPrimitive,
                q18: TwoUnitPrimitive,
                q19: TwoUnitPrimitive,
                q20: TwoUnitPrimitive,
                q21: TwoUnitPrimitive,
                q22: TwoUnitPrimitive,
                q23: TwoUnitPrimitive,
                q24: TwoUnitPrimitive,
                q25: TwoUnitPrimitive,
                q26: TwoUnitPrimitive,
                q27: TwoUnitPrimitive,
                q28: TwoUnitPrimitive,
                q29: TwoUnitPrimitive,
                q30: TwoUnitPrimitive,
                q31: TwoUnitPrimitive,
                q32: TwoUnitPrimitive,
                q33: TwoUnitPrimitive,
                q34: TwoUnitPrimitive,
                q35: TwoUnitPrimitive,
                q36: TwoUnitPrimitive,
                q37: TwoUnitPrimitive,
                q38: TwoUnitPrimitive,
                q39: TwoUnitPrimitive,
                q40: TwoUnitPrimitive,
                q41: TwoUnitPrimitive,
                q42: TwoUnitPrimitive,
                q43: TwoUnitPrimitive,
                q44: TwoUnitPrimitive,
                q45: TwoUnitPrimitive,
                q46: TwoUnitPrimitive,
                q47: TwoUnitPrimitive,
                q48: TwoUnitPrimitive,
                q49: TwoUnitPrimitive,
                q50: TwoUnitPrimitive,
                q51: TwoUnitPrimitive,
                q52: TwoUnitPrimitive,
                q53: TwoUnitPrimitive,
                q54: TwoUnitPrimitive,
                q55: TwoUnitPrimitive,
                q56: TwoUnitPrimitive,
                q57: TwoUnitPrimitive,
                q58: TwoUnitPrimitive,
                q59: TwoUnitPrimitive,
                q60: TwoUnitPrimitive,
                q61: TwoUnitPrimitive,
                q62: TwoUnitPrimitive,
                q63: TwoUnitPrimitive,
            ) = Dst64(
                q00,
                q01,
                q02,
                q03,
                q04,
                q05,
                q06,
                q07,
                q08,
                q09,
                q10,
                q11,
                q12,
                q13,
                q14,
                q15,
                q16,
                q17,
                q18,
                q19,
                q20,
                q21,
                q22,
                q23,
                q24,
                q25,
                q26,
                q27,
                q28,
                q29,
                q30,
                q31,
                q32,
                q33,
                q34,
                q35,
                q36,
                q37,
                q38,
                q39,
                q40,
                q41,
                q42,
                q43,
                q44,
                q45,
                q46,
                q47,
                q48,
                q49,
                q50,
                q51,
                q52,
                q53,
                q54,
                q55,
                q56,
                q57,
                q58,
                q59,
                q60,
                q61,
                q62,
                q63,
            )
        }
    }

    @Test
    fun test64() {
        val expected = callPrimaryConstructor<Dst64> { TwoUnitPrimitive(it.index.toLong()) }
        val src = replacePQ(defaultMapper.writeValueAsString(expected))
        Assertions.assertEquals(expected, defaultMapper.readValue<Dst64>(src))
    }

    data class Dst65(
        val p00: TwoUnitPrimitive,
        val p01: TwoUnitPrimitive,
        val p02: TwoUnitPrimitive,
        val p03: TwoUnitPrimitive,
        val p04: TwoUnitPrimitive,
        val p05: TwoUnitPrimitive,
        val p06: TwoUnitPrimitive,
        val p07: TwoUnitPrimitive,
        val p08: TwoUnitPrimitive,
        val p09: TwoUnitPrimitive,
        val p10: TwoUnitPrimitive,
        val p11: TwoUnitPrimitive,
        val p12: TwoUnitPrimitive,
        val p13: TwoUnitPrimitive,
        val p14: TwoUnitPrimitive,
        val p15: TwoUnitPrimitive,
        val p16: TwoUnitPrimitive,
        val p17: TwoUnitPrimitive,
        val p18: TwoUnitPrimitive,
        val p19: TwoUnitPrimitive,
        val p20: TwoUnitPrimitive,
        val p21: TwoUnitPrimitive,
        val p22: TwoUnitPrimitive,
        val p23: TwoUnitPrimitive,
        val p24: TwoUnitPrimitive,
        val p25: TwoUnitPrimitive,
        val p26: TwoUnitPrimitive,
        val p27: TwoUnitPrimitive,
        val p28: TwoUnitPrimitive,
        val p29: TwoUnitPrimitive,
        val p30: TwoUnitPrimitive,
        val p31: TwoUnitPrimitive,
        val p32: TwoUnitPrimitive,
        val p33: TwoUnitPrimitive,
        val p34: TwoUnitPrimitive,
        val p35: TwoUnitPrimitive,
        val p36: TwoUnitPrimitive,
        val p37: TwoUnitPrimitive,
        val p38: TwoUnitPrimitive,
        val p39: TwoUnitPrimitive,
        val p40: TwoUnitPrimitive,
        val p41: TwoUnitPrimitive,
        val p42: TwoUnitPrimitive,
        val p43: TwoUnitPrimitive,
        val p44: TwoUnitPrimitive,
        val p45: TwoUnitPrimitive,
        val p46: TwoUnitPrimitive,
        val p47: TwoUnitPrimitive,
        val p48: TwoUnitPrimitive,
        val p49: TwoUnitPrimitive,
        val p50: TwoUnitPrimitive,
        val p51: TwoUnitPrimitive,
        val p52: TwoUnitPrimitive,
        val p53: TwoUnitPrimitive,
        val p54: TwoUnitPrimitive,
        val p55: TwoUnitPrimitive,
        val p56: TwoUnitPrimitive,
        val p57: TwoUnitPrimitive,
        val p58: TwoUnitPrimitive,
        val p59: TwoUnitPrimitive,
        val p60: TwoUnitPrimitive,
        val p61: TwoUnitPrimitive,
        val p62: TwoUnitPrimitive,
        val p63: TwoUnitPrimitive,
        val p64: TwoUnitPrimitive,
    ) {
        companion object {
            @JvmStatic
            @JsonCreator
            fun creator(
                q00: TwoUnitPrimitive,
                q01: TwoUnitPrimitive,
                q02: TwoUnitPrimitive,
                q03: TwoUnitPrimitive,
                q04: TwoUnitPrimitive,
                q05: TwoUnitPrimitive,
                q06: TwoUnitPrimitive,
                q07: TwoUnitPrimitive,
                q08: TwoUnitPrimitive,
                q09: TwoUnitPrimitive,
                q10: TwoUnitPrimitive,
                q11: TwoUnitPrimitive,
                q12: TwoUnitPrimitive,
                q13: TwoUnitPrimitive,
                q14: TwoUnitPrimitive,
                q15: TwoUnitPrimitive,
                q16: TwoUnitPrimitive,
                q17: TwoUnitPrimitive,
                q18: TwoUnitPrimitive,
                q19: TwoUnitPrimitive,
                q20: TwoUnitPrimitive,
                q21: TwoUnitPrimitive,
                q22: TwoUnitPrimitive,
                q23: TwoUnitPrimitive,
                q24: TwoUnitPrimitive,
                q25: TwoUnitPrimitive,
                q26: TwoUnitPrimitive,
                q27: TwoUnitPrimitive,
                q28: TwoUnitPrimitive,
                q29: TwoUnitPrimitive,
                q30: TwoUnitPrimitive,
                q31: TwoUnitPrimitive,
                q32: TwoUnitPrimitive,
                q33: TwoUnitPrimitive,
                q34: TwoUnitPrimitive,
                q35: TwoUnitPrimitive,
                q36: TwoUnitPrimitive,
                q37: TwoUnitPrimitive,
                q38: TwoUnitPrimitive,
                q39: TwoUnitPrimitive,
                q40: TwoUnitPrimitive,
                q41: TwoUnitPrimitive,
                q42: TwoUnitPrimitive,
                q43: TwoUnitPrimitive,
                q44: TwoUnitPrimitive,
                q45: TwoUnitPrimitive,
                q46: TwoUnitPrimitive,
                q47: TwoUnitPrimitive,
                q48: TwoUnitPrimitive,
                q49: TwoUnitPrimitive,
                q50: TwoUnitPrimitive,
                q51: TwoUnitPrimitive,
                q52: TwoUnitPrimitive,
                q53: TwoUnitPrimitive,
                q54: TwoUnitPrimitive,
                q55: TwoUnitPrimitive,
                q56: TwoUnitPrimitive,
                q57: TwoUnitPrimitive,
                q58: TwoUnitPrimitive,
                q59: TwoUnitPrimitive,
                q60: TwoUnitPrimitive,
                q61: TwoUnitPrimitive,
                q62: TwoUnitPrimitive,
                q63: TwoUnitPrimitive,
                q64: TwoUnitPrimitive,
            ) = Dst65(
                q00,
                q01,
                q02,
                q03,
                q04,
                q05,
                q06,
                q07,
                q08,
                q09,
                q10,
                q11,
                q12,
                q13,
                q14,
                q15,
                q16,
                q17,
                q18,
                q19,
                q20,
                q21,
                q22,
                q23,
                q24,
                q25,
                q26,
                q27,
                q28,
                q29,
                q30,
                q31,
                q32,
                q33,
                q34,
                q35,
                q36,
                q37,
                q38,
                q39,
                q40,
                q41,
                q42,
                q43,
                q44,
                q45,
                q46,
                q47,
                q48,
                q49,
                q50,
                q51,
                q52,
                q53,
                q54,
                q55,
                q56,
                q57,
                q58,
                q59,
                q60,
                q61,
                q62,
                q63,
                q64,
            )
        }
    }

    @Test
    fun test65() {
        val expected = callPrimaryConstructor<Dst65> { TwoUnitPrimitive(it.index.toLong()) }
        val src = replacePQ(defaultMapper.writeValueAsString(expected))
        Assertions.assertEquals(expected, defaultMapper.readValue<Dst65>(src))
    }

    // It cannot be a data class because the generated method would exceed the argument size limit.
    class DstMax(
        val p000: TwoUnitPrimitive,
        val p001: TwoUnitPrimitive,
        val p002: TwoUnitPrimitive,
        val p003: TwoUnitPrimitive,
        val p004: TwoUnitPrimitive,
        val p005: TwoUnitPrimitive,
        val p006: TwoUnitPrimitive,
        val p007: TwoUnitPrimitive,
        val p008: TwoUnitPrimitive,
        val p009: TwoUnitPrimitive,
        val p010: TwoUnitPrimitive,
        val p011: TwoUnitPrimitive,
        val p012: TwoUnitPrimitive,
        val p013: TwoUnitPrimitive,
        val p014: TwoUnitPrimitive,
        val p015: TwoUnitPrimitive,
        val p016: TwoUnitPrimitive,
        val p017: TwoUnitPrimitive,
        val p018: TwoUnitPrimitive,
        val p019: TwoUnitPrimitive,
        val p020: TwoUnitPrimitive,
        val p021: TwoUnitPrimitive,
        val p022: TwoUnitPrimitive,
        val p023: TwoUnitPrimitive,
        val p024: TwoUnitPrimitive,
        val p025: TwoUnitPrimitive,
        val p026: TwoUnitPrimitive,
        val p027: TwoUnitPrimitive,
        val p028: TwoUnitPrimitive,
        val p029: TwoUnitPrimitive,
        val p030: TwoUnitPrimitive,
        val p031: TwoUnitPrimitive,
        val p032: TwoUnitPrimitive,
        val p033: TwoUnitPrimitive,
        val p034: TwoUnitPrimitive,
        val p035: TwoUnitPrimitive,
        val p036: TwoUnitPrimitive,
        val p037: TwoUnitPrimitive,
        val p038: TwoUnitPrimitive,
        val p039: TwoUnitPrimitive,
        val p040: TwoUnitPrimitive,
        val p041: TwoUnitPrimitive,
        val p042: TwoUnitPrimitive,
        val p043: TwoUnitPrimitive,
        val p044: TwoUnitPrimitive,
        val p045: TwoUnitPrimitive,
        val p046: TwoUnitPrimitive,
        val p047: TwoUnitPrimitive,
        val p048: TwoUnitPrimitive,
        val p049: TwoUnitPrimitive,
        val p050: TwoUnitPrimitive,
        val p051: TwoUnitPrimitive,
        val p052: TwoUnitPrimitive,
        val p053: TwoUnitPrimitive,
        val p054: TwoUnitPrimitive,
        val p055: TwoUnitPrimitive,
        val p056: TwoUnitPrimitive,
        val p057: TwoUnitPrimitive,
        val p058: TwoUnitPrimitive,
        val p059: TwoUnitPrimitive,
        val p060: TwoUnitPrimitive,
        val p061: TwoUnitPrimitive,
        val p062: TwoUnitPrimitive,
        val p063: TwoUnitPrimitive,
        val p064: TwoUnitPrimitive,
        val p065: TwoUnitPrimitive,
        val p066: TwoUnitPrimitive,
        val p067: TwoUnitPrimitive,
        val p068: TwoUnitPrimitive,
        val p069: TwoUnitPrimitive,
        val p070: TwoUnitPrimitive,
        val p071: TwoUnitPrimitive,
        val p072: TwoUnitPrimitive,
        val p073: TwoUnitPrimitive,
        val p074: TwoUnitPrimitive,
        val p075: TwoUnitPrimitive,
        val p076: TwoUnitPrimitive,
        val p077: TwoUnitPrimitive,
        val p078: TwoUnitPrimitive,
        val p079: TwoUnitPrimitive,
        val p080: TwoUnitPrimitive,
        val p081: TwoUnitPrimitive,
        val p082: TwoUnitPrimitive,
        val p083: TwoUnitPrimitive,
        val p084: TwoUnitPrimitive,
        val p085: TwoUnitPrimitive,
        val p086: TwoUnitPrimitive,
        val p087: TwoUnitPrimitive,
        val p088: TwoUnitPrimitive,
        val p089: TwoUnitPrimitive,
        val p090: TwoUnitPrimitive,
        val p091: TwoUnitPrimitive,
        val p092: TwoUnitPrimitive,
        val p093: TwoUnitPrimitive,
        val p094: TwoUnitPrimitive,
        val p095: TwoUnitPrimitive,
        val p096: TwoUnitPrimitive,
        val p097: TwoUnitPrimitive,
        val p098: TwoUnitPrimitive,
        val p099: TwoUnitPrimitive,
        val p100: TwoUnitPrimitive,
        val p101: TwoUnitPrimitive,
        val p102: TwoUnitPrimitive,
        val p103: TwoUnitPrimitive,
        val p104: TwoUnitPrimitive,
        val p105: TwoUnitPrimitive,
        val p106: TwoUnitPrimitive,
        val p107: TwoUnitPrimitive,
        val p108: TwoUnitPrimitive,
        val p109: TwoUnitPrimitive,
        val p110: TwoUnitPrimitive,
        val p111: TwoUnitPrimitive,
        val p112: TwoUnitPrimitive,
        val p113: TwoUnitPrimitive,
        val p114: TwoUnitPrimitive,
        val p115: TwoUnitPrimitive,
        val p116: TwoUnitPrimitive,
        val p117: TwoUnitPrimitive,
        val p118: TwoUnitPrimitive,
        val p119: TwoUnitPrimitive,
        val p120: TwoUnitPrimitive,
        val p121: TwoUnitPrimitive,
        val p122: TwoUnitPrimitive,
        val p123: TwoUnitPrimitive,
        val p124: TwoUnitPrimitive,
        val p125: TwoUnitPrimitive,
    ) {
        companion object {
            @JvmStatic
            @JsonCreator
            fun creator(
                q000: TwoUnitPrimitive,
                q001: TwoUnitPrimitive,
                q002: TwoUnitPrimitive,
                q003: TwoUnitPrimitive,
                q004: TwoUnitPrimitive,
                q005: TwoUnitPrimitive,
                q006: TwoUnitPrimitive,
                q007: TwoUnitPrimitive,
                q008: TwoUnitPrimitive,
                q009: TwoUnitPrimitive,
                q010: TwoUnitPrimitive,
                q011: TwoUnitPrimitive,
                q012: TwoUnitPrimitive,
                q013: TwoUnitPrimitive,
                q014: TwoUnitPrimitive,
                q015: TwoUnitPrimitive,
                q016: TwoUnitPrimitive,
                q017: TwoUnitPrimitive,
                q018: TwoUnitPrimitive,
                q019: TwoUnitPrimitive,
                q020: TwoUnitPrimitive,
                q021: TwoUnitPrimitive,
                q022: TwoUnitPrimitive,
                q023: TwoUnitPrimitive,
                q024: TwoUnitPrimitive,
                q025: TwoUnitPrimitive,
                q026: TwoUnitPrimitive,
                q027: TwoUnitPrimitive,
                q028: TwoUnitPrimitive,
                q029: TwoUnitPrimitive,
                q030: TwoUnitPrimitive,
                q031: TwoUnitPrimitive,
                q032: TwoUnitPrimitive,
                q033: TwoUnitPrimitive,
                q034: TwoUnitPrimitive,
                q035: TwoUnitPrimitive,
                q036: TwoUnitPrimitive,
                q037: TwoUnitPrimitive,
                q038: TwoUnitPrimitive,
                q039: TwoUnitPrimitive,
                q040: TwoUnitPrimitive,
                q041: TwoUnitPrimitive,
                q042: TwoUnitPrimitive,
                q043: TwoUnitPrimitive,
                q044: TwoUnitPrimitive,
                q045: TwoUnitPrimitive,
                q046: TwoUnitPrimitive,
                q047: TwoUnitPrimitive,
                q048: TwoUnitPrimitive,
                q049: TwoUnitPrimitive,
                q050: TwoUnitPrimitive,
                q051: TwoUnitPrimitive,
                q052: TwoUnitPrimitive,
                q053: TwoUnitPrimitive,
                q054: TwoUnitPrimitive,
                q055: TwoUnitPrimitive,
                q056: TwoUnitPrimitive,
                q057: TwoUnitPrimitive,
                q058: TwoUnitPrimitive,
                q059: TwoUnitPrimitive,
                q060: TwoUnitPrimitive,
                q061: TwoUnitPrimitive,
                q062: TwoUnitPrimitive,
                q063: TwoUnitPrimitive,
                q064: TwoUnitPrimitive,
                q065: TwoUnitPrimitive,
                q066: TwoUnitPrimitive,
                q067: TwoUnitPrimitive,
                q068: TwoUnitPrimitive,
                q069: TwoUnitPrimitive,
                q070: TwoUnitPrimitive,
                q071: TwoUnitPrimitive,
                q072: TwoUnitPrimitive,
                q073: TwoUnitPrimitive,
                q074: TwoUnitPrimitive,
                q075: TwoUnitPrimitive,
                q076: TwoUnitPrimitive,
                q077: TwoUnitPrimitive,
                q078: TwoUnitPrimitive,
                q079: TwoUnitPrimitive,
                q080: TwoUnitPrimitive,
                q081: TwoUnitPrimitive,
                q082: TwoUnitPrimitive,
                q083: TwoUnitPrimitive,
                q084: TwoUnitPrimitive,
                q085: TwoUnitPrimitive,
                q086: TwoUnitPrimitive,
                q087: TwoUnitPrimitive,
                q088: TwoUnitPrimitive,
                q089: TwoUnitPrimitive,
                q090: TwoUnitPrimitive,
                q091: TwoUnitPrimitive,
                q092: TwoUnitPrimitive,
                q093: TwoUnitPrimitive,
                q094: TwoUnitPrimitive,
                q095: TwoUnitPrimitive,
                q096: TwoUnitPrimitive,
                q097: TwoUnitPrimitive,
                q098: TwoUnitPrimitive,
                q099: TwoUnitPrimitive,
                q100: TwoUnitPrimitive,
                q101: TwoUnitPrimitive,
                q102: TwoUnitPrimitive,
                q103: TwoUnitPrimitive,
                q104: TwoUnitPrimitive,
                q105: TwoUnitPrimitive,
                q106: TwoUnitPrimitive,
                q107: TwoUnitPrimitive,
                q108: TwoUnitPrimitive,
                q109: TwoUnitPrimitive,
                q110: TwoUnitPrimitive,
                q111: TwoUnitPrimitive,
                q112: TwoUnitPrimitive,
                q113: TwoUnitPrimitive,
                q114: TwoUnitPrimitive,
                q115: TwoUnitPrimitive,
                q116: TwoUnitPrimitive,
                q117: TwoUnitPrimitive,
                q118: TwoUnitPrimitive,
                q119: TwoUnitPrimitive,
                q120: TwoUnitPrimitive,
                q121: TwoUnitPrimitive,
                q122: TwoUnitPrimitive,
                q123: TwoUnitPrimitive,
                q124: TwoUnitPrimitive,
                q125: TwoUnitPrimitive,
            ) = DstMax(
                q000,
                q001,
                q002,
                q003,
                q004,
                q005,
                q006,
                q007,
                q008,
                q009,
                q010,
                q011,
                q012,
                q013,
                q014,
                q015,
                q016,
                q017,
                q018,
                q019,
                q020,
                q021,
                q022,
                q023,
                q024,
                q025,
                q026,
                q027,
                q028,
                q029,
                q030,
                q031,
                q032,
                q033,
                q034,
                q035,
                q036,
                q037,
                q038,
                q039,
                q040,
                q041,
                q042,
                q043,
                q044,
                q045,
                q046,
                q047,
                q048,
                q049,
                q050,
                q051,
                q052,
                q053,
                q054,
                q055,
                q056,
                q057,
                q058,
                q059,
                q060,
                q061,
                q062,
                q063,
                q064,
                q065,
                q066,
                q067,
                q068,
                q069,
                q070,
                q071,
                q072,
                q073,
                q074,
                q075,
                q076,
                q077,
                q078,
                q079,
                q080,
                q081,
                q082,
                q083,
                q084,
                q085,
                q086,
                q087,
                q088,
                q089,
                q090,
                q091,
                q092,
                q093,
                q094,
                q095,
                q096,
                q097,
                q098,
                q099,
                q100,
                q101,
                q102,
                q103,
                q104,
                q105,
                q106,
                q107,
                q108,
                q109,
                q110,
                q111,
                q112,
                q113,
                q114,
                q115,
                q116,
                q117,
                q118,
                q119,
                q120,
                q121,
                q122,
                q123,
                q124,
                q125,
            )
        }
    }

    @Test
    fun testMax() {
        val expected = callPrimaryConstructor<DstMax> { TwoUnitPrimitive(it.index.toLong()) }
        val src = replacePQ(defaultMapper.writeValueAsString(expected))
        assertReflectEquals(expected, defaultMapper.readValue<DstMax>(src))
    }
}
