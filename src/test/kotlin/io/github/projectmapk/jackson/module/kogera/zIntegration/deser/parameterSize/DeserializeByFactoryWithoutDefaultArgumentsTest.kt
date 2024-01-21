package io.github.projectmapk.jackson.module.kogera.zIntegration.deser.parameterSize

import com.fasterxml.jackson.annotation.JsonCreator
import io.github.projectmapk.jackson.module.kogera.assertReflectEquals
import io.github.projectmapk.jackson.module.kogera.callPrimaryConstructor
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

// Convert the property p to q (but not the value) to make it an input to the factory function.
private fun replacePQ(src: String) = src.replace(Regex("""p\d+":""")) { "q" + it.value.substring(1) }

/**
 * Up to argument size 32 there is one mask argument for the default argument,
 * 33 ~ 64 there are two, and 65 there are three, so each boundary value is tested.
 * Also, the maximum argument size that can be set in the constructor is 254, so that case is tested as well.
 */
class DeserializeByFactoryWithoutDefaultArgumentsTest {
    data class Dst32(
        val p00: String,
        val p01: String,
        val p02: String,
        val p03: String,
        val p04: String,
        val p05: String,
        val p06: String,
        val p07: String,
        val p08: String,
        val p09: String,
        val p10: String,
        val p11: String,
        val p12: String,
        val p13: String,
        val p14: String,
        val p15: String,
        val p16: String,
        val p17: String,
        val p18: String,
        val p19: String,
        val p20: String,
        val p21: String,
        val p22: String,
        val p23: String,
        val p24: String,
        val p25: String,
        val p26: String,
        val p27: String,
        val p28: String,
        val p29: String,
        val p30: String,
        val p31: String
    ) {
        companion object {
            @JvmStatic
            @JsonCreator
            fun creator(
                q00: String,
                q01: String,
                q02: String,
                q03: String,
                q04: String,
                q05: String,
                q06: String,
                q07: String,
                q08: String,
                q09: String,
                q10: String,
                q11: String,
                q12: String,
                q13: String,
                q14: String,
                q15: String,
                q16: String,
                q17: String,
                q18: String,
                q19: String,
                q20: String,
                q21: String,
                q22: String,
                q23: String,
                q24: String,
                q25: String,
                q26: String,
                q27: String,
                q28: String,
                q29: String,
                q30: String,
                q31: String
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
                q31
            )
        }
    }

    @Test
    fun test32() {
        val expected = callPrimaryConstructor<Dst32>()
        val src = replacePQ(defaultMapper.writeValueAsString(expected))
        Assertions.assertEquals(expected, defaultMapper.readValue<Dst32>(src))
    }

    data class Dst33(
        val p00: String,
        val p01: String,
        val p02: String,
        val p03: String,
        val p04: String,
        val p05: String,
        val p06: String,
        val p07: String,
        val p08: String,
        val p09: String,
        val p10: String,
        val p11: String,
        val p12: String,
        val p13: String,
        val p14: String,
        val p15: String,
        val p16: String,
        val p17: String,
        val p18: String,
        val p19: String,
        val p20: String,
        val p21: String,
        val p22: String,
        val p23: String,
        val p24: String,
        val p25: String,
        val p26: String,
        val p27: String,
        val p28: String,
        val p29: String,
        val p30: String,
        val p31: String,
        val p32: String
    ) {
        companion object {
            @JvmStatic
            @JsonCreator
            fun creator(
                q00: String,
                q01: String,
                q02: String,
                q03: String,
                q04: String,
                q05: String,
                q06: String,
                q07: String,
                q08: String,
                q09: String,
                q10: String,
                q11: String,
                q12: String,
                q13: String,
                q14: String,
                q15: String,
                q16: String,
                q17: String,
                q18: String,
                q19: String,
                q20: String,
                q21: String,
                q22: String,
                q23: String,
                q24: String,
                q25: String,
                q26: String,
                q27: String,
                q28: String,
                q29: String,
                q30: String,
                q31: String,
                q32: String
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
                q32
            )
        }
    }

    @Test
    fun test33() {
        val expected = callPrimaryConstructor<Dst33>()
        val src = replacePQ(defaultMapper.writeValueAsString(expected))
        Assertions.assertEquals(expected, defaultMapper.readValue<Dst33>(src))
    }

    data class Dst64(
        val p00: String,
        val p01: String,
        val p02: String,
        val p03: String,
        val p04: String,
        val p05: String,
        val p06: String,
        val p07: String,
        val p08: String,
        val p09: String,
        val p10: String,
        val p11: String,
        val p12: String,
        val p13: String,
        val p14: String,
        val p15: String,
        val p16: String,
        val p17: String,
        val p18: String,
        val p19: String,
        val p20: String,
        val p21: String,
        val p22: String,
        val p23: String,
        val p24: String,
        val p25: String,
        val p26: String,
        val p27: String,
        val p28: String,
        val p29: String,
        val p30: String,
        val p31: String,
        val p32: String,
        val p33: String,
        val p34: String,
        val p35: String,
        val p36: String,
        val p37: String,
        val p38: String,
        val p39: String,
        val p40: String,
        val p41: String,
        val p42: String,
        val p43: String,
        val p44: String,
        val p45: String,
        val p46: String,
        val p47: String,
        val p48: String,
        val p49: String,
        val p50: String,
        val p51: String,
        val p52: String,
        val p53: String,
        val p54: String,
        val p55: String,
        val p56: String,
        val p57: String,
        val p58: String,
        val p59: String,
        val p60: String,
        val p61: String,
        val p62: String,
        val p63: String
    ) {
        companion object {
            @JvmStatic
            @JsonCreator
            fun creator(
                q00: String,
                q01: String,
                q02: String,
                q03: String,
                q04: String,
                q05: String,
                q06: String,
                q07: String,
                q08: String,
                q09: String,
                q10: String,
                q11: String,
                q12: String,
                q13: String,
                q14: String,
                q15: String,
                q16: String,
                q17: String,
                q18: String,
                q19: String,
                q20: String,
                q21: String,
                q22: String,
                q23: String,
                q24: String,
                q25: String,
                q26: String,
                q27: String,
                q28: String,
                q29: String,
                q30: String,
                q31: String,
                q32: String,
                q33: String,
                q34: String,
                q35: String,
                q36: String,
                q37: String,
                q38: String,
                q39: String,
                q40: String,
                q41: String,
                q42: String,
                q43: String,
                q44: String,
                q45: String,
                q46: String,
                q47: String,
                q48: String,
                q49: String,
                q50: String,
                q51: String,
                q52: String,
                q53: String,
                q54: String,
                q55: String,
                q56: String,
                q57: String,
                q58: String,
                q59: String,
                q60: String,
                q61: String,
                q62: String,
                q63: String
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
                q63
            )
        }
    }

    @Test
    fun test64() {
        val expected = callPrimaryConstructor<Dst64>()
        val src = replacePQ(defaultMapper.writeValueAsString(expected))
        Assertions.assertEquals(expected, defaultMapper.readValue<Dst64>(src))
    }

    data class Dst65(
        val p00: String,
        val p01: String,
        val p02: String,
        val p03: String,
        val p04: String,
        val p05: String,
        val p06: String,
        val p07: String,
        val p08: String,
        val p09: String,
        val p10: String,
        val p11: String,
        val p12: String,
        val p13: String,
        val p14: String,
        val p15: String,
        val p16: String,
        val p17: String,
        val p18: String,
        val p19: String,
        val p20: String,
        val p21: String,
        val p22: String,
        val p23: String,
        val p24: String,
        val p25: String,
        val p26: String,
        val p27: String,
        val p28: String,
        val p29: String,
        val p30: String,
        val p31: String,
        val p32: String,
        val p33: String,
        val p34: String,
        val p35: String,
        val p36: String,
        val p37: String,
        val p38: String,
        val p39: String,
        val p40: String,
        val p41: String,
        val p42: String,
        val p43: String,
        val p44: String,
        val p45: String,
        val p46: String,
        val p47: String,
        val p48: String,
        val p49: String,
        val p50: String,
        val p51: String,
        val p52: String,
        val p53: String,
        val p54: String,
        val p55: String,
        val p56: String,
        val p57: String,
        val p58: String,
        val p59: String,
        val p60: String,
        val p61: String,
        val p62: String,
        val p63: String,
        val p64: String
    ) {
        companion object {
            @JvmStatic
            @JsonCreator
            fun creator(
                q00: String,
                q01: String,
                q02: String,
                q03: String,
                q04: String,
                q05: String,
                q06: String,
                q07: String,
                q08: String,
                q09: String,
                q10: String,
                q11: String,
                q12: String,
                q13: String,
                q14: String,
                q15: String,
                q16: String,
                q17: String,
                q18: String,
                q19: String,
                q20: String,
                q21: String,
                q22: String,
                q23: String,
                q24: String,
                q25: String,
                q26: String,
                q27: String,
                q28: String,
                q29: String,
                q30: String,
                q31: String,
                q32: String,
                q33: String,
                q34: String,
                q35: String,
                q36: String,
                q37: String,
                q38: String,
                q39: String,
                q40: String,
                q41: String,
                q42: String,
                q43: String,
                q44: String,
                q45: String,
                q46: String,
                q47: String,
                q48: String,
                q49: String,
                q50: String,
                q51: String,
                q52: String,
                q53: String,
                q54: String,
                q55: String,
                q56: String,
                q57: String,
                q58: String,
                q59: String,
                q60: String,
                q61: String,
                q62: String,
                q63: String,
                q64: String
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
                q64
            )
        }
    }

    @Test
    fun test65() {
        val expected = callPrimaryConstructor<Dst65>()
        val src = replacePQ(defaultMapper.writeValueAsString(expected))
        Assertions.assertEquals(expected, defaultMapper.readValue<Dst65>(src))
    }

    // It cannot be a data class because the generated method would exceed the argument size limit.
    class DstMax(
        val p000: String,
        val p001: String,
        val p002: String,
        val p003: String,
        val p004: String,
        val p005: String,
        val p006: String,
        val p007: String,
        val p008: String,
        val p009: String,
        val p010: String,
        val p011: String,
        val p012: String,
        val p013: String,
        val p014: String,
        val p015: String,
        val p016: String,
        val p017: String,
        val p018: String,
        val p019: String,
        val p020: String,
        val p021: String,
        val p022: String,
        val p023: String,
        val p024: String,
        val p025: String,
        val p026: String,
        val p027: String,
        val p028: String,
        val p029: String,
        val p030: String,
        val p031: String,
        val p032: String,
        val p033: String,
        val p034: String,
        val p035: String,
        val p036: String,
        val p037: String,
        val p038: String,
        val p039: String,
        val p040: String,
        val p041: String,
        val p042: String,
        val p043: String,
        val p044: String,
        val p045: String,
        val p046: String,
        val p047: String,
        val p048: String,
        val p049: String,
        val p050: String,
        val p051: String,
        val p052: String,
        val p053: String,
        val p054: String,
        val p055: String,
        val p056: String,
        val p057: String,
        val p058: String,
        val p059: String,
        val p060: String,
        val p061: String,
        val p062: String,
        val p063: String,
        val p064: String,
        val p065: String,
        val p066: String,
        val p067: String,
        val p068: String,
        val p069: String,
        val p070: String,
        val p071: String,
        val p072: String,
        val p073: String,
        val p074: String,
        val p075: String,
        val p076: String,
        val p077: String,
        val p078: String,
        val p079: String,
        val p080: String,
        val p081: String,
        val p082: String,
        val p083: String,
        val p084: String,
        val p085: String,
        val p086: String,
        val p087: String,
        val p088: String,
        val p089: String,
        val p090: String,
        val p091: String,
        val p092: String,
        val p093: String,
        val p094: String,
        val p095: String,
        val p096: String,
        val p097: String,
        val p098: String,
        val p099: String,
        val p100: String,
        val p101: String,
        val p102: String,
        val p103: String,
        val p104: String,
        val p105: String,
        val p106: String,
        val p107: String,
        val p108: String,
        val p109: String,
        val p110: String,
        val p111: String,
        val p112: String,
        val p113: String,
        val p114: String,
        val p115: String,
        val p116: String,
        val p117: String,
        val p118: String,
        val p119: String,
        val p120: String,
        val p121: String,
        val p122: String,
        val p123: String,
        val p124: String,
        val p125: String,
        val p126: String,
        val p127: String,
        val p128: String,
        val p129: String,
        val p130: String,
        val p131: String,
        val p132: String,
        val p133: String,
        val p134: String,
        val p135: String,
        val p136: String,
        val p137: String,
        val p138: String,
        val p139: String,
        val p140: String,
        val p141: String,
        val p142: String,
        val p143: String,
        val p144: String,
        val p145: String,
        val p146: String,
        val p147: String,
        val p148: String,
        val p149: String,
        val p150: String,
        val p151: String,
        val p152: String,
        val p153: String,
        val p154: String,
        val p155: String,
        val p156: String,
        val p157: String,
        val p158: String,
        val p159: String,
        val p160: String,
        val p161: String,
        val p162: String,
        val p163: String,
        val p164: String,
        val p165: String,
        val p166: String,
        val p167: String,
        val p168: String,
        val p169: String,
        val p170: String,
        val p171: String,
        val p172: String,
        val p173: String,
        val p174: String,
        val p175: String,
        val p176: String,
        val p177: String,
        val p178: String,
        val p179: String,
        val p180: String,
        val p181: String,
        val p182: String,
        val p183: String,
        val p184: String,
        val p185: String,
        val p186: String,
        val p187: String,
        val p188: String,
        val p189: String,
        val p190: String,
        val p191: String,
        val p192: String,
        val p193: String,
        val p194: String,
        val p195: String,
        val p196: String,
        val p197: String,
        val p198: String,
        val p199: String,
        val p200: String,
        val p201: String,
        val p202: String,
        val p203: String,
        val p204: String,
        val p205: String,
        val p206: String,
        val p207: String,
        val p208: String,
        val p209: String,
        val p210: String,
        val p211: String,
        val p212: String,
        val p213: String,
        val p214: String,
        val p215: String,
        val p216: String,
        val p217: String,
        val p218: String,
        val p219: String,
        val p220: String,
        val p221: String,
        val p222: String,
        val p223: String,
        val p224: String,
        val p225: String,
        val p226: String,
        val p227: String,
        val p228: String,
        val p229: String,
        val p230: String,
        val p231: String,
        val p232: String,
        val p233: String,
        val p234: String,
        val p235: String,
        val p236: String,
        val p237: String,
        val p238: String,
        val p239: String,
        val p240: String,
        val p241: String,
        val p242: String,
        val p243: String,
        val p244: String,
        val p245: String,
        val p246: String,
        val p247: String,
        val p248: String,
        val p249: String,
        val p250: String,
        val p251: String,
        val p252: String,
        val p253: String
    ) {
        companion object {
            @JvmStatic
            @JsonCreator
            fun creator(
                q000: String,
                q001: String,
                q002: String,
                q003: String,
                q004: String,
                q005: String,
                q006: String,
                q007: String,
                q008: String,
                q009: String,
                q010: String,
                q011: String,
                q012: String,
                q013: String,
                q014: String,
                q015: String,
                q016: String,
                q017: String,
                q018: String,
                q019: String,
                q020: String,
                q021: String,
                q022: String,
                q023: String,
                q024: String,
                q025: String,
                q026: String,
                q027: String,
                q028: String,
                q029: String,
                q030: String,
                q031: String,
                q032: String,
                q033: String,
                q034: String,
                q035: String,
                q036: String,
                q037: String,
                q038: String,
                q039: String,
                q040: String,
                q041: String,
                q042: String,
                q043: String,
                q044: String,
                q045: String,
                q046: String,
                q047: String,
                q048: String,
                q049: String,
                q050: String,
                q051: String,
                q052: String,
                q053: String,
                q054: String,
                q055: String,
                q056: String,
                q057: String,
                q058: String,
                q059: String,
                q060: String,
                q061: String,
                q062: String,
                q063: String,
                q064: String,
                q065: String,
                q066: String,
                q067: String,
                q068: String,
                q069: String,
                q070: String,
                q071: String,
                q072: String,
                q073: String,
                q074: String,
                q075: String,
                q076: String,
                q077: String,
                q078: String,
                q079: String,
                q080: String,
                q081: String,
                q082: String,
                q083: String,
                q084: String,
                q085: String,
                q086: String,
                q087: String,
                q088: String,
                q089: String,
                q090: String,
                q091: String,
                q092: String,
                q093: String,
                q094: String,
                q095: String,
                q096: String,
                q097: String,
                q098: String,
                q099: String,
                q100: String,
                q101: String,
                q102: String,
                q103: String,
                q104: String,
                q105: String,
                q106: String,
                q107: String,
                q108: String,
                q109: String,
                q110: String,
                q111: String,
                q112: String,
                q113: String,
                q114: String,
                q115: String,
                q116: String,
                q117: String,
                q118: String,
                q119: String,
                q120: String,
                q121: String,
                q122: String,
                q123: String,
                q124: String,
                q125: String,
                q126: String,
                q127: String,
                q128: String,
                q129: String,
                q130: String,
                q131: String,
                q132: String,
                q133: String,
                q134: String,
                q135: String,
                q136: String,
                q137: String,
                q138: String,
                q139: String,
                q140: String,
                q141: String,
                q142: String,
                q143: String,
                q144: String,
                q145: String,
                q146: String,
                q147: String,
                q148: String,
                q149: String,
                q150: String,
                q151: String,
                q152: String,
                q153: String,
                q154: String,
                q155: String,
                q156: String,
                q157: String,
                q158: String,
                q159: String,
                q160: String,
                q161: String,
                q162: String,
                q163: String,
                q164: String,
                q165: String,
                q166: String,
                q167: String,
                q168: String,
                q169: String,
                q170: String,
                q171: String,
                q172: String,
                q173: String,
                q174: String,
                q175: String,
                q176: String,
                q177: String,
                q178: String,
                q179: String,
                q180: String,
                q181: String,
                q182: String,
                q183: String,
                q184: String,
                q185: String,
                q186: String,
                q187: String,
                q188: String,
                q189: String,
                q190: String,
                q191: String,
                q192: String,
                q193: String,
                q194: String,
                q195: String,
                q196: String,
                q197: String,
                q198: String,
                q199: String,
                q200: String,
                q201: String,
                q202: String,
                q203: String,
                q204: String,
                q205: String,
                q206: String,
                q207: String,
                q208: String,
                q209: String,
                q210: String,
                q211: String,
                q212: String,
                q213: String,
                q214: String,
                q215: String,
                q216: String,
                q217: String,
                q218: String,
                q219: String,
                q220: String,
                q221: String,
                q222: String,
                q223: String,
                q224: String,
                q225: String,
                q226: String,
                q227: String,
                q228: String,
                q229: String,
                q230: String,
                q231: String,
                q232: String,
                q233: String,
                q234: String,
                q235: String,
                q236: String,
                q237: String,
                q238: String,
                q239: String,
                q240: String,
                q241: String,
                q242: String,
                q243: String,
                q244: String,
                q245: String,
                q246: String,
                q247: String,
                q248: String,
                q249: String,
                q250: String,
                q251: String,
                q252: String,
                q253: String
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
                q126,
                q127,
                q128,
                q129,
                q130,
                q131,
                q132,
                q133,
                q134,
                q135,
                q136,
                q137,
                q138,
                q139,
                q140,
                q141,
                q142,
                q143,
                q144,
                q145,
                q146,
                q147,
                q148,
                q149,
                q150,
                q151,
                q152,
                q153,
                q154,
                q155,
                q156,
                q157,
                q158,
                q159,
                q160,
                q161,
                q162,
                q163,
                q164,
                q165,
                q166,
                q167,
                q168,
                q169,
                q170,
                q171,
                q172,
                q173,
                q174,
                q175,
                q176,
                q177,
                q178,
                q179,
                q180,
                q181,
                q182,
                q183,
                q184,
                q185,
                q186,
                q187,
                q188,
                q189,
                q190,
                q191,
                q192,
                q193,
                q194,
                q195,
                q196,
                q197,
                q198,
                q199,
                q200,
                q201,
                q202,
                q203,
                q204,
                q205,
                q206,
                q207,
                q208,
                q209,
                q210,
                q211,
                q212,
                q213,
                q214,
                q215,
                q216,
                q217,
                q218,
                q219,
                q220,
                q221,
                q222,
                q223,
                q224,
                q225,
                q226,
                q227,
                q228,
                q229,
                q230,
                q231,
                q232,
                q233,
                q234,
                q235,
                q236,
                q237,
                q238,
                q239,
                q240,
                q241,
                q242,
                q243,
                q244,
                q245,
                q246,
                q247,
                q248,
                q249,
                q250,
                q251,
                q252,
                q253
            )
        }
    }

    @Test
    fun testMax() {
        val expected = callPrimaryConstructor<DstMax>()
        val src = replacePQ(defaultMapper.writeValueAsString(expected))
        assertReflectEquals(expected, defaultMapper.readValue<DstMax>(src))
    }
}
