package io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.parameterSize.primitive

import io.github.projectmapk.jackson.module.kogera.assertReflectEquals
import io.github.projectmapk.jackson.module.kogera.callPrimaryConstructor
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.Primitive
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Up to argument size 32 there is one mask argument for the default argument,
 * 33 ~ 64 there are two, and 65 there are three, so each boundary value is tested.
 * Also, the maximum size of arguments that can be set by a constructor
 * that includes a value class as an argument is 253 (one less by DefaultConstructorMarker), so test that case as well.
 */
class DeserializeByConstructorWithoutDefaultArgumentsTest {
    data class Dst32(
        val p00: Primitive,
        val p01: Primitive,
        val p02: Primitive,
        val p03: Primitive,
        val p04: Primitive,
        val p05: Primitive,
        val p06: Primitive,
        val p07: Primitive,
        val p08: Primitive,
        val p09: Primitive,
        val p10: Primitive,
        val p11: Primitive,
        val p12: Primitive,
        val p13: Primitive,
        val p14: Primitive,
        val p15: Primitive,
        val p16: Primitive,
        val p17: Primitive,
        val p18: Primitive,
        val p19: Primitive,
        val p20: Primitive,
        val p21: Primitive,
        val p22: Primitive,
        val p23: Primitive,
        val p24: Primitive,
        val p25: Primitive,
        val p26: Primitive,
        val p27: Primitive,
        val p28: Primitive,
        val p29: Primitive,
        val p30: Primitive,
        val p31: Primitive
    )

    @Test
    fun test32() {
        val expected = callPrimaryConstructor<Dst32> { Primitive(it.index) }
        val src = defaultMapper.writeValueAsString(expected)
        Assertions.assertEquals(expected, defaultMapper.readValue<Dst32>(src))
    }

    data class Dst33(
        val p00: Primitive,
        val p01: Primitive,
        val p02: Primitive,
        val p03: Primitive,
        val p04: Primitive,
        val p05: Primitive,
        val p06: Primitive,
        val p07: Primitive,
        val p08: Primitive,
        val p09: Primitive,
        val p10: Primitive,
        val p11: Primitive,
        val p12: Primitive,
        val p13: Primitive,
        val p14: Primitive,
        val p15: Primitive,
        val p16: Primitive,
        val p17: Primitive,
        val p18: Primitive,
        val p19: Primitive,
        val p20: Primitive,
        val p21: Primitive,
        val p22: Primitive,
        val p23: Primitive,
        val p24: Primitive,
        val p25: Primitive,
        val p26: Primitive,
        val p27: Primitive,
        val p28: Primitive,
        val p29: Primitive,
        val p30: Primitive,
        val p31: Primitive,
        val p32: Primitive
    )

    @Test
    fun test33() {
        val expected = callPrimaryConstructor<Dst33> { Primitive(it.index) }
        val src = defaultMapper.writeValueAsString(expected)
        Assertions.assertEquals(expected, defaultMapper.readValue<Dst33>(src))
    }

    data class Dst64(
        val p00: Primitive,
        val p01: Primitive,
        val p02: Primitive,
        val p03: Primitive,
        val p04: Primitive,
        val p05: Primitive,
        val p06: Primitive,
        val p07: Primitive,
        val p08: Primitive,
        val p09: Primitive,
        val p10: Primitive,
        val p11: Primitive,
        val p12: Primitive,
        val p13: Primitive,
        val p14: Primitive,
        val p15: Primitive,
        val p16: Primitive,
        val p17: Primitive,
        val p18: Primitive,
        val p19: Primitive,
        val p20: Primitive,
        val p21: Primitive,
        val p22: Primitive,
        val p23: Primitive,
        val p24: Primitive,
        val p25: Primitive,
        val p26: Primitive,
        val p27: Primitive,
        val p28: Primitive,
        val p29: Primitive,
        val p30: Primitive,
        val p31: Primitive,
        val p32: Primitive,
        val p33: Primitive,
        val p34: Primitive,
        val p35: Primitive,
        val p36: Primitive,
        val p37: Primitive,
        val p38: Primitive,
        val p39: Primitive,
        val p40: Primitive,
        val p41: Primitive,
        val p42: Primitive,
        val p43: Primitive,
        val p44: Primitive,
        val p45: Primitive,
        val p46: Primitive,
        val p47: Primitive,
        val p48: Primitive,
        val p49: Primitive,
        val p50: Primitive,
        val p51: Primitive,
        val p52: Primitive,
        val p53: Primitive,
        val p54: Primitive,
        val p55: Primitive,
        val p56: Primitive,
        val p57: Primitive,
        val p58: Primitive,
        val p59: Primitive,
        val p60: Primitive,
        val p61: Primitive,
        val p62: Primitive,
        val p63: Primitive
    )

    @Test
    fun test64() {
        val expected = callPrimaryConstructor<Dst64> { Primitive(it.index) }
        val src = defaultMapper.writeValueAsString(expected)
        Assertions.assertEquals(expected, defaultMapper.readValue<Dst64>(src))
    }

    data class Dst65(
        val p00: Primitive,
        val p01: Primitive,
        val p02: Primitive,
        val p03: Primitive,
        val p04: Primitive,
        val p05: Primitive,
        val p06: Primitive,
        val p07: Primitive,
        val p08: Primitive,
        val p09: Primitive,
        val p10: Primitive,
        val p11: Primitive,
        val p12: Primitive,
        val p13: Primitive,
        val p14: Primitive,
        val p15: Primitive,
        val p16: Primitive,
        val p17: Primitive,
        val p18: Primitive,
        val p19: Primitive,
        val p20: Primitive,
        val p21: Primitive,
        val p22: Primitive,
        val p23: Primitive,
        val p24: Primitive,
        val p25: Primitive,
        val p26: Primitive,
        val p27: Primitive,
        val p28: Primitive,
        val p29: Primitive,
        val p30: Primitive,
        val p31: Primitive,
        val p32: Primitive,
        val p33: Primitive,
        val p34: Primitive,
        val p35: Primitive,
        val p36: Primitive,
        val p37: Primitive,
        val p38: Primitive,
        val p39: Primitive,
        val p40: Primitive,
        val p41: Primitive,
        val p42: Primitive,
        val p43: Primitive,
        val p44: Primitive,
        val p45: Primitive,
        val p46: Primitive,
        val p47: Primitive,
        val p48: Primitive,
        val p49: Primitive,
        val p50: Primitive,
        val p51: Primitive,
        val p52: Primitive,
        val p53: Primitive,
        val p54: Primitive,
        val p55: Primitive,
        val p56: Primitive,
        val p57: Primitive,
        val p58: Primitive,
        val p59: Primitive,
        val p60: Primitive,
        val p61: Primitive,
        val p62: Primitive,
        val p63: Primitive,
        val p64: Primitive
    )

    @Test
    fun test65() {
        val expected = callPrimaryConstructor<Dst65> { Primitive(it.index) }
        val src = defaultMapper.writeValueAsString(expected)
        Assertions.assertEquals(expected, defaultMapper.readValue<Dst65>(src))
    }

    // It cannot be a data class because the generated method would exceed the argument size limit.
    class DstMax(
        val p000: Primitive,
        val p001: Primitive,
        val p002: Primitive,
        val p003: Primitive,
        val p004: Primitive,
        val p005: Primitive,
        val p006: Primitive,
        val p007: Primitive,
        val p008: Primitive,
        val p009: Primitive,
        val p010: Primitive,
        val p011: Primitive,
        val p012: Primitive,
        val p013: Primitive,
        val p014: Primitive,
        val p015: Primitive,
        val p016: Primitive,
        val p017: Primitive,
        val p018: Primitive,
        val p019: Primitive,
        val p020: Primitive,
        val p021: Primitive,
        val p022: Primitive,
        val p023: Primitive,
        val p024: Primitive,
        val p025: Primitive,
        val p026: Primitive,
        val p027: Primitive,
        val p028: Primitive,
        val p029: Primitive,
        val p030: Primitive,
        val p031: Primitive,
        val p032: Primitive,
        val p033: Primitive,
        val p034: Primitive,
        val p035: Primitive,
        val p036: Primitive,
        val p037: Primitive,
        val p038: Primitive,
        val p039: Primitive,
        val p040: Primitive,
        val p041: Primitive,
        val p042: Primitive,
        val p043: Primitive,
        val p044: Primitive,
        val p045: Primitive,
        val p046: Primitive,
        val p047: Primitive,
        val p048: Primitive,
        val p049: Primitive,
        val p050: Primitive,
        val p051: Primitive,
        val p052: Primitive,
        val p053: Primitive,
        val p054: Primitive,
        val p055: Primitive,
        val p056: Primitive,
        val p057: Primitive,
        val p058: Primitive,
        val p059: Primitive,
        val p060: Primitive,
        val p061: Primitive,
        val p062: Primitive,
        val p063: Primitive,
        val p064: Primitive,
        val p065: Primitive,
        val p066: Primitive,
        val p067: Primitive,
        val p068: Primitive,
        val p069: Primitive,
        val p070: Primitive,
        val p071: Primitive,
        val p072: Primitive,
        val p073: Primitive,
        val p074: Primitive,
        val p075: Primitive,
        val p076: Primitive,
        val p077: Primitive,
        val p078: Primitive,
        val p079: Primitive,
        val p080: Primitive,
        val p081: Primitive,
        val p082: Primitive,
        val p083: Primitive,
        val p084: Primitive,
        val p085: Primitive,
        val p086: Primitive,
        val p087: Primitive,
        val p088: Primitive,
        val p089: Primitive,
        val p090: Primitive,
        val p091: Primitive,
        val p092: Primitive,
        val p093: Primitive,
        val p094: Primitive,
        val p095: Primitive,
        val p096: Primitive,
        val p097: Primitive,
        val p098: Primitive,
        val p099: Primitive,
        val p100: Primitive,
        val p101: Primitive,
        val p102: Primitive,
        val p103: Primitive,
        val p104: Primitive,
        val p105: Primitive,
        val p106: Primitive,
        val p107: Primitive,
        val p108: Primitive,
        val p109: Primitive,
        val p110: Primitive,
        val p111: Primitive,
        val p112: Primitive,
        val p113: Primitive,
        val p114: Primitive,
        val p115: Primitive,
        val p116: Primitive,
        val p117: Primitive,
        val p118: Primitive,
        val p119: Primitive,
        val p120: Primitive,
        val p121: Primitive,
        val p122: Primitive,
        val p123: Primitive,
        val p124: Primitive,
        val p125: Primitive,
        val p126: Primitive,
        val p127: Primitive,
        val p128: Primitive,
        val p129: Primitive,
        val p130: Primitive,
        val p131: Primitive,
        val p132: Primitive,
        val p133: Primitive,
        val p134: Primitive,
        val p135: Primitive,
        val p136: Primitive,
        val p137: Primitive,
        val p138: Primitive,
        val p139: Primitive,
        val p140: Primitive,
        val p141: Primitive,
        val p142: Primitive,
        val p143: Primitive,
        val p144: Primitive,
        val p145: Primitive,
        val p146: Primitive,
        val p147: Primitive,
        val p148: Primitive,
        val p149: Primitive,
        val p150: Primitive,
        val p151: Primitive,
        val p152: Primitive,
        val p153: Primitive,
        val p154: Primitive,
        val p155: Primitive,
        val p156: Primitive,
        val p157: Primitive,
        val p158: Primitive,
        val p159: Primitive,
        val p160: Primitive,
        val p161: Primitive,
        val p162: Primitive,
        val p163: Primitive,
        val p164: Primitive,
        val p165: Primitive,
        val p166: Primitive,
        val p167: Primitive,
        val p168: Primitive,
        val p169: Primitive,
        val p170: Primitive,
        val p171: Primitive,
        val p172: Primitive,
        val p173: Primitive,
        val p174: Primitive,
        val p175: Primitive,
        val p176: Primitive,
        val p177: Primitive,
        val p178: Primitive,
        val p179: Primitive,
        val p180: Primitive,
        val p181: Primitive,
        val p182: Primitive,
        val p183: Primitive,
        val p184: Primitive,
        val p185: Primitive,
        val p186: Primitive,
        val p187: Primitive,
        val p188: Primitive,
        val p189: Primitive,
        val p190: Primitive,
        val p191: Primitive,
        val p192: Primitive,
        val p193: Primitive,
        val p194: Primitive,
        val p195: Primitive,
        val p196: Primitive,
        val p197: Primitive,
        val p198: Primitive,
        val p199: Primitive,
        val p200: Primitive,
        val p201: Primitive,
        val p202: Primitive,
        val p203: Primitive,
        val p204: Primitive,
        val p205: Primitive,
        val p206: Primitive,
        val p207: Primitive,
        val p208: Primitive,
        val p209: Primitive,
        val p210: Primitive,
        val p211: Primitive,
        val p212: Primitive,
        val p213: Primitive,
        val p214: Primitive,
        val p215: Primitive,
        val p216: Primitive,
        val p217: Primitive,
        val p218: Primitive,
        val p219: Primitive,
        val p220: Primitive,
        val p221: Primitive,
        val p222: Primitive,
        val p223: Primitive,
        val p224: Primitive,
        val p225: Primitive,
        val p226: Primitive,
        val p227: Primitive,
        val p228: Primitive,
        val p229: Primitive,
        val p230: Primitive,
        val p231: Primitive,
        val p232: Primitive,
        val p233: Primitive,
        val p234: Primitive,
        val p235: Primitive,
        val p236: Primitive,
        val p237: Primitive,
        val p238: Primitive,
        val p239: Primitive,
        val p240: Primitive,
        val p241: Primitive,
        val p242: Primitive,
        val p243: Primitive,
        val p244: Primitive,
        val p245: Primitive,
        val p246: Primitive,
        val p247: Primitive,
        val p248: Primitive,
        val p249: Primitive,
        val p250: Primitive,
        val p251: Primitive,
        val p252: Primitive
    )

    @Test
    fun testMax() {
        val expected = callPrimaryConstructor<DstMax> { Primitive(it.index) }
        val src = defaultMapper.writeValueAsString(expected)
        assertReflectEquals(expected, defaultMapper.readValue<DstMax>(src))
    }
}