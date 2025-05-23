package io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.parameterSize.nullablePrimitive

import io.github.projectmapk.jackson.module.kogera.assertReflectEquals
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.NullablePrimitive
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Up to argument size 32 there is one mask argument for the default argument,
 * 33 ~ 64 there are two, and 65 there are three, so each boundary value is tested.
 * Also, if the default argument is set, the maximum argument size that can be set in the constructor is 245,
 * so that case is tested as well.
 */
class DeserializeByConstructorWithDefaultArgumentsTest {
    data class Dst32(
        val p00: NullablePrimitive? = NullablePrimitive(0),
        val p01: NullablePrimitive? = NullablePrimitive(1),
        val p02: NullablePrimitive? = NullablePrimitive(2),
        val p03: NullablePrimitive? = NullablePrimitive(3),
        val p04: NullablePrimitive? = NullablePrimitive(4),
        val p05: NullablePrimitive? = NullablePrimitive(5),
        val p06: NullablePrimitive? = NullablePrimitive(6),
        val p07: NullablePrimitive? = NullablePrimitive(7),
        val p08: NullablePrimitive? = NullablePrimitive(8),
        val p09: NullablePrimitive? = NullablePrimitive(9),
        val p10: NullablePrimitive? = NullablePrimitive(10),
        val p11: NullablePrimitive? = NullablePrimitive(11),
        val p12: NullablePrimitive? = NullablePrimitive(12),
        val p13: NullablePrimitive? = NullablePrimitive(13),
        val p14: NullablePrimitive? = NullablePrimitive(14),
        val p15: NullablePrimitive? = NullablePrimitive(15),
        val p16: NullablePrimitive? = NullablePrimitive(16),
        val p17: NullablePrimitive? = NullablePrimitive(17),
        val p18: NullablePrimitive? = NullablePrimitive(18),
        val p19: NullablePrimitive? = NullablePrimitive(19),
        val p20: NullablePrimitive? = NullablePrimitive(20),
        val p21: NullablePrimitive? = NullablePrimitive(21),
        val p22: NullablePrimitive? = NullablePrimitive(22),
        val p23: NullablePrimitive? = NullablePrimitive(23),
        val p24: NullablePrimitive? = NullablePrimitive(24),
        val p25: NullablePrimitive? = NullablePrimitive(25),
        val p26: NullablePrimitive? = NullablePrimitive(26),
        val p27: NullablePrimitive? = NullablePrimitive(27),
        val p28: NullablePrimitive? = NullablePrimitive(28),
        val p29: NullablePrimitive? = NullablePrimitive(29),
        val p30: NullablePrimitive? = NullablePrimitive(30),
        val p31: NullablePrimitive? = NullablePrimitive(31),
    )

    @Test
    fun test32() {
        Assertions.assertEquals(Dst32(), defaultMapper.readValue<Dst32>("{}"))
    }

    data class Dst33(
        val p00: NullablePrimitive? = NullablePrimitive(0),
        val p01: NullablePrimitive? = NullablePrimitive(1),
        val p02: NullablePrimitive? = NullablePrimitive(2),
        val p03: NullablePrimitive? = NullablePrimitive(3),
        val p04: NullablePrimitive? = NullablePrimitive(4),
        val p05: NullablePrimitive? = NullablePrimitive(5),
        val p06: NullablePrimitive? = NullablePrimitive(6),
        val p07: NullablePrimitive? = NullablePrimitive(7),
        val p08: NullablePrimitive? = NullablePrimitive(8),
        val p09: NullablePrimitive? = NullablePrimitive(9),
        val p10: NullablePrimitive? = NullablePrimitive(10),
        val p11: NullablePrimitive? = NullablePrimitive(11),
        val p12: NullablePrimitive? = NullablePrimitive(12),
        val p13: NullablePrimitive? = NullablePrimitive(13),
        val p14: NullablePrimitive? = NullablePrimitive(14),
        val p15: NullablePrimitive? = NullablePrimitive(15),
        val p16: NullablePrimitive? = NullablePrimitive(16),
        val p17: NullablePrimitive? = NullablePrimitive(17),
        val p18: NullablePrimitive? = NullablePrimitive(18),
        val p19: NullablePrimitive? = NullablePrimitive(19),
        val p20: NullablePrimitive? = NullablePrimitive(20),
        val p21: NullablePrimitive? = NullablePrimitive(21),
        val p22: NullablePrimitive? = NullablePrimitive(22),
        val p23: NullablePrimitive? = NullablePrimitive(23),
        val p24: NullablePrimitive? = NullablePrimitive(24),
        val p25: NullablePrimitive? = NullablePrimitive(25),
        val p26: NullablePrimitive? = NullablePrimitive(26),
        val p27: NullablePrimitive? = NullablePrimitive(27),
        val p28: NullablePrimitive? = NullablePrimitive(28),
        val p29: NullablePrimitive? = NullablePrimitive(29),
        val p30: NullablePrimitive? = NullablePrimitive(30),
        val p31: NullablePrimitive? = NullablePrimitive(31),
        val p32: NullablePrimitive? = NullablePrimitive(32),
    )

    @Test
    fun test33() {
        Assertions.assertEquals(Dst33(), defaultMapper.readValue<Dst33>("{}"))
    }

    data class Dst64(
        val p00: NullablePrimitive? = NullablePrimitive(0),
        val p01: NullablePrimitive? = NullablePrimitive(1),
        val p02: NullablePrimitive? = NullablePrimitive(2),
        val p03: NullablePrimitive? = NullablePrimitive(3),
        val p04: NullablePrimitive? = NullablePrimitive(4),
        val p05: NullablePrimitive? = NullablePrimitive(5),
        val p06: NullablePrimitive? = NullablePrimitive(6),
        val p07: NullablePrimitive? = NullablePrimitive(7),
        val p08: NullablePrimitive? = NullablePrimitive(8),
        val p09: NullablePrimitive? = NullablePrimitive(9),
        val p10: NullablePrimitive? = NullablePrimitive(10),
        val p11: NullablePrimitive? = NullablePrimitive(11),
        val p12: NullablePrimitive? = NullablePrimitive(12),
        val p13: NullablePrimitive? = NullablePrimitive(13),
        val p14: NullablePrimitive? = NullablePrimitive(14),
        val p15: NullablePrimitive? = NullablePrimitive(15),
        val p16: NullablePrimitive? = NullablePrimitive(16),
        val p17: NullablePrimitive? = NullablePrimitive(17),
        val p18: NullablePrimitive? = NullablePrimitive(18),
        val p19: NullablePrimitive? = NullablePrimitive(19),
        val p20: NullablePrimitive? = NullablePrimitive(20),
        val p21: NullablePrimitive? = NullablePrimitive(21),
        val p22: NullablePrimitive? = NullablePrimitive(22),
        val p23: NullablePrimitive? = NullablePrimitive(23),
        val p24: NullablePrimitive? = NullablePrimitive(24),
        val p25: NullablePrimitive? = NullablePrimitive(25),
        val p26: NullablePrimitive? = NullablePrimitive(26),
        val p27: NullablePrimitive? = NullablePrimitive(27),
        val p28: NullablePrimitive? = NullablePrimitive(28),
        val p29: NullablePrimitive? = NullablePrimitive(29),
        val p30: NullablePrimitive? = NullablePrimitive(30),
        val p31: NullablePrimitive? = NullablePrimitive(31),
        val p32: NullablePrimitive? = NullablePrimitive(32),
        val p33: NullablePrimitive? = NullablePrimitive(33),
        val p34: NullablePrimitive? = NullablePrimitive(34),
        val p35: NullablePrimitive? = NullablePrimitive(35),
        val p36: NullablePrimitive? = NullablePrimitive(36),
        val p37: NullablePrimitive? = NullablePrimitive(37),
        val p38: NullablePrimitive? = NullablePrimitive(38),
        val p39: NullablePrimitive? = NullablePrimitive(39),
        val p40: NullablePrimitive? = NullablePrimitive(40),
        val p41: NullablePrimitive? = NullablePrimitive(41),
        val p42: NullablePrimitive? = NullablePrimitive(42),
        val p43: NullablePrimitive? = NullablePrimitive(43),
        val p44: NullablePrimitive? = NullablePrimitive(44),
        val p45: NullablePrimitive? = NullablePrimitive(45),
        val p46: NullablePrimitive? = NullablePrimitive(46),
        val p47: NullablePrimitive? = NullablePrimitive(47),
        val p48: NullablePrimitive? = NullablePrimitive(48),
        val p49: NullablePrimitive? = NullablePrimitive(49),
        val p50: NullablePrimitive? = NullablePrimitive(50),
        val p51: NullablePrimitive? = NullablePrimitive(51),
        val p52: NullablePrimitive? = NullablePrimitive(52),
        val p53: NullablePrimitive? = NullablePrimitive(53),
        val p54: NullablePrimitive? = NullablePrimitive(54),
        val p55: NullablePrimitive? = NullablePrimitive(55),
        val p56: NullablePrimitive? = NullablePrimitive(56),
        val p57: NullablePrimitive? = NullablePrimitive(57),
        val p58: NullablePrimitive? = NullablePrimitive(58),
        val p59: NullablePrimitive? = NullablePrimitive(59),
        val p60: NullablePrimitive? = NullablePrimitive(60),
        val p61: NullablePrimitive? = NullablePrimitive(61),
        val p62: NullablePrimitive? = NullablePrimitive(62),
        val p63: NullablePrimitive? = NullablePrimitive(63),
    )

    @Test
    fun test64() {
        Assertions.assertEquals(Dst64(), defaultMapper.readValue<Dst64>("{}"))
    }

    data class Dst65(
        val p00: NullablePrimitive? = NullablePrimitive(0),
        val p01: NullablePrimitive? = NullablePrimitive(1),
        val p02: NullablePrimitive? = NullablePrimitive(2),
        val p03: NullablePrimitive? = NullablePrimitive(3),
        val p04: NullablePrimitive? = NullablePrimitive(4),
        val p05: NullablePrimitive? = NullablePrimitive(5),
        val p06: NullablePrimitive? = NullablePrimitive(6),
        val p07: NullablePrimitive? = NullablePrimitive(7),
        val p08: NullablePrimitive? = NullablePrimitive(8),
        val p09: NullablePrimitive? = NullablePrimitive(9),
        val p10: NullablePrimitive? = NullablePrimitive(10),
        val p11: NullablePrimitive? = NullablePrimitive(11),
        val p12: NullablePrimitive? = NullablePrimitive(12),
        val p13: NullablePrimitive? = NullablePrimitive(13),
        val p14: NullablePrimitive? = NullablePrimitive(14),
        val p15: NullablePrimitive? = NullablePrimitive(15),
        val p16: NullablePrimitive? = NullablePrimitive(16),
        val p17: NullablePrimitive? = NullablePrimitive(17),
        val p18: NullablePrimitive? = NullablePrimitive(18),
        val p19: NullablePrimitive? = NullablePrimitive(19),
        val p20: NullablePrimitive? = NullablePrimitive(20),
        val p21: NullablePrimitive? = NullablePrimitive(21),
        val p22: NullablePrimitive? = NullablePrimitive(22),
        val p23: NullablePrimitive? = NullablePrimitive(23),
        val p24: NullablePrimitive? = NullablePrimitive(24),
        val p25: NullablePrimitive? = NullablePrimitive(25),
        val p26: NullablePrimitive? = NullablePrimitive(26),
        val p27: NullablePrimitive? = NullablePrimitive(27),
        val p28: NullablePrimitive? = NullablePrimitive(28),
        val p29: NullablePrimitive? = NullablePrimitive(29),
        val p30: NullablePrimitive? = NullablePrimitive(30),
        val p31: NullablePrimitive? = NullablePrimitive(31),
        val p32: NullablePrimitive? = NullablePrimitive(32),
        val p33: NullablePrimitive? = NullablePrimitive(33),
        val p34: NullablePrimitive? = NullablePrimitive(34),
        val p35: NullablePrimitive? = NullablePrimitive(35),
        val p36: NullablePrimitive? = NullablePrimitive(36),
        val p37: NullablePrimitive? = NullablePrimitive(37),
        val p38: NullablePrimitive? = NullablePrimitive(38),
        val p39: NullablePrimitive? = NullablePrimitive(39),
        val p40: NullablePrimitive? = NullablePrimitive(40),
        val p41: NullablePrimitive? = NullablePrimitive(41),
        val p42: NullablePrimitive? = NullablePrimitive(42),
        val p43: NullablePrimitive? = NullablePrimitive(43),
        val p44: NullablePrimitive? = NullablePrimitive(44),
        val p45: NullablePrimitive? = NullablePrimitive(45),
        val p46: NullablePrimitive? = NullablePrimitive(46),
        val p47: NullablePrimitive? = NullablePrimitive(47),
        val p48: NullablePrimitive? = NullablePrimitive(48),
        val p49: NullablePrimitive? = NullablePrimitive(49),
        val p50: NullablePrimitive? = NullablePrimitive(50),
        val p51: NullablePrimitive? = NullablePrimitive(51),
        val p52: NullablePrimitive? = NullablePrimitive(52),
        val p53: NullablePrimitive? = NullablePrimitive(53),
        val p54: NullablePrimitive? = NullablePrimitive(54),
        val p55: NullablePrimitive? = NullablePrimitive(55),
        val p56: NullablePrimitive? = NullablePrimitive(56),
        val p57: NullablePrimitive? = NullablePrimitive(57),
        val p58: NullablePrimitive? = NullablePrimitive(58),
        val p59: NullablePrimitive? = NullablePrimitive(59),
        val p60: NullablePrimitive? = NullablePrimitive(60),
        val p61: NullablePrimitive? = NullablePrimitive(61),
        val p62: NullablePrimitive? = NullablePrimitive(62),
        val p63: NullablePrimitive? = NullablePrimitive(63),
        val p64: NullablePrimitive? = NullablePrimitive(64),
    )

    @Test
    fun test65() {
        Assertions.assertEquals(Dst65(), defaultMapper.readValue<Dst65>("{}"))
    }

    // It cannot be a data class because the generated method would exceed the argument size limit.
    class DstMax(
        val p000: NullablePrimitive? = NullablePrimitive(0),
        val p001: NullablePrimitive? = NullablePrimitive(1),
        val p002: NullablePrimitive? = NullablePrimitive(2),
        val p003: NullablePrimitive? = NullablePrimitive(3),
        val p004: NullablePrimitive? = NullablePrimitive(4),
        val p005: NullablePrimitive? = NullablePrimitive(5),
        val p006: NullablePrimitive? = NullablePrimitive(6),
        val p007: NullablePrimitive? = NullablePrimitive(7),
        val p008: NullablePrimitive? = NullablePrimitive(8),
        val p009: NullablePrimitive? = NullablePrimitive(9),
        val p010: NullablePrimitive? = NullablePrimitive(10),
        val p011: NullablePrimitive? = NullablePrimitive(11),
        val p012: NullablePrimitive? = NullablePrimitive(12),
        val p013: NullablePrimitive? = NullablePrimitive(13),
        val p014: NullablePrimitive? = NullablePrimitive(14),
        val p015: NullablePrimitive? = NullablePrimitive(15),
        val p016: NullablePrimitive? = NullablePrimitive(16),
        val p017: NullablePrimitive? = NullablePrimitive(17),
        val p018: NullablePrimitive? = NullablePrimitive(18),
        val p019: NullablePrimitive? = NullablePrimitive(19),
        val p020: NullablePrimitive? = NullablePrimitive(20),
        val p021: NullablePrimitive? = NullablePrimitive(21),
        val p022: NullablePrimitive? = NullablePrimitive(22),
        val p023: NullablePrimitive? = NullablePrimitive(23),
        val p024: NullablePrimitive? = NullablePrimitive(24),
        val p025: NullablePrimitive? = NullablePrimitive(25),
        val p026: NullablePrimitive? = NullablePrimitive(26),
        val p027: NullablePrimitive? = NullablePrimitive(27),
        val p028: NullablePrimitive? = NullablePrimitive(28),
        val p029: NullablePrimitive? = NullablePrimitive(29),
        val p030: NullablePrimitive? = NullablePrimitive(30),
        val p031: NullablePrimitive? = NullablePrimitive(31),
        val p032: NullablePrimitive? = NullablePrimitive(32),
        val p033: NullablePrimitive? = NullablePrimitive(33),
        val p034: NullablePrimitive? = NullablePrimitive(34),
        val p035: NullablePrimitive? = NullablePrimitive(35),
        val p036: NullablePrimitive? = NullablePrimitive(36),
        val p037: NullablePrimitive? = NullablePrimitive(37),
        val p038: NullablePrimitive? = NullablePrimitive(38),
        val p039: NullablePrimitive? = NullablePrimitive(39),
        val p040: NullablePrimitive? = NullablePrimitive(40),
        val p041: NullablePrimitive? = NullablePrimitive(41),
        val p042: NullablePrimitive? = NullablePrimitive(42),
        val p043: NullablePrimitive? = NullablePrimitive(43),
        val p044: NullablePrimitive? = NullablePrimitive(44),
        val p045: NullablePrimitive? = NullablePrimitive(45),
        val p046: NullablePrimitive? = NullablePrimitive(46),
        val p047: NullablePrimitive? = NullablePrimitive(47),
        val p048: NullablePrimitive? = NullablePrimitive(48),
        val p049: NullablePrimitive? = NullablePrimitive(49),
        val p050: NullablePrimitive? = NullablePrimitive(50),
        val p051: NullablePrimitive? = NullablePrimitive(51),
        val p052: NullablePrimitive? = NullablePrimitive(52),
        val p053: NullablePrimitive? = NullablePrimitive(53),
        val p054: NullablePrimitive? = NullablePrimitive(54),
        val p055: NullablePrimitive? = NullablePrimitive(55),
        val p056: NullablePrimitive? = NullablePrimitive(56),
        val p057: NullablePrimitive? = NullablePrimitive(57),
        val p058: NullablePrimitive? = NullablePrimitive(58),
        val p059: NullablePrimitive? = NullablePrimitive(59),
        val p060: NullablePrimitive? = NullablePrimitive(60),
        val p061: NullablePrimitive? = NullablePrimitive(61),
        val p062: NullablePrimitive? = NullablePrimitive(62),
        val p063: NullablePrimitive? = NullablePrimitive(63),
        val p064: NullablePrimitive? = NullablePrimitive(64),
        val p065: NullablePrimitive? = NullablePrimitive(65),
        val p066: NullablePrimitive? = NullablePrimitive(66),
        val p067: NullablePrimitive? = NullablePrimitive(67),
        val p068: NullablePrimitive? = NullablePrimitive(68),
        val p069: NullablePrimitive? = NullablePrimitive(69),
        val p070: NullablePrimitive? = NullablePrimitive(70),
        val p071: NullablePrimitive? = NullablePrimitive(71),
        val p072: NullablePrimitive? = NullablePrimitive(72),
        val p073: NullablePrimitive? = NullablePrimitive(73),
        val p074: NullablePrimitive? = NullablePrimitive(74),
        val p075: NullablePrimitive? = NullablePrimitive(75),
        val p076: NullablePrimitive? = NullablePrimitive(76),
        val p077: NullablePrimitive? = NullablePrimitive(77),
        val p078: NullablePrimitive? = NullablePrimitive(78),
        val p079: NullablePrimitive? = NullablePrimitive(79),
        val p080: NullablePrimitive? = NullablePrimitive(80),
        val p081: NullablePrimitive? = NullablePrimitive(81),
        val p082: NullablePrimitive? = NullablePrimitive(82),
        val p083: NullablePrimitive? = NullablePrimitive(83),
        val p084: NullablePrimitive? = NullablePrimitive(84),
        val p085: NullablePrimitive? = NullablePrimitive(85),
        val p086: NullablePrimitive? = NullablePrimitive(86),
        val p087: NullablePrimitive? = NullablePrimitive(87),
        val p088: NullablePrimitive? = NullablePrimitive(88),
        val p089: NullablePrimitive? = NullablePrimitive(89),
        val p090: NullablePrimitive? = NullablePrimitive(90),
        val p091: NullablePrimitive? = NullablePrimitive(91),
        val p092: NullablePrimitive? = NullablePrimitive(92),
        val p093: NullablePrimitive? = NullablePrimitive(93),
        val p094: NullablePrimitive? = NullablePrimitive(94),
        val p095: NullablePrimitive? = NullablePrimitive(95),
        val p096: NullablePrimitive? = NullablePrimitive(96),
        val p097: NullablePrimitive? = NullablePrimitive(97),
        val p098: NullablePrimitive? = NullablePrimitive(98),
        val p099: NullablePrimitive? = NullablePrimitive(99),
        val p100: NullablePrimitive? = NullablePrimitive(100),
        val p101: NullablePrimitive? = NullablePrimitive(101),
        val p102: NullablePrimitive? = NullablePrimitive(102),
        val p103: NullablePrimitive? = NullablePrimitive(103),
        val p104: NullablePrimitive? = NullablePrimitive(104),
        val p105: NullablePrimitive? = NullablePrimitive(105),
        val p106: NullablePrimitive? = NullablePrimitive(106),
        val p107: NullablePrimitive? = NullablePrimitive(107),
        val p108: NullablePrimitive? = NullablePrimitive(108),
        val p109: NullablePrimitive? = NullablePrimitive(109),
        val p110: NullablePrimitive? = NullablePrimitive(110),
        val p111: NullablePrimitive? = NullablePrimitive(111),
        val p112: NullablePrimitive? = NullablePrimitive(112),
        val p113: NullablePrimitive? = NullablePrimitive(113),
        val p114: NullablePrimitive? = NullablePrimitive(114),
        val p115: NullablePrimitive? = NullablePrimitive(115),
        val p116: NullablePrimitive? = NullablePrimitive(116),
        val p117: NullablePrimitive? = NullablePrimitive(117),
        val p118: NullablePrimitive? = NullablePrimitive(118),
        val p119: NullablePrimitive? = NullablePrimitive(119),
        val p120: NullablePrimitive? = NullablePrimitive(120),
        val p121: NullablePrimitive? = NullablePrimitive(121),
        val p122: NullablePrimitive? = NullablePrimitive(122),
        val p123: NullablePrimitive? = NullablePrimitive(123),
        val p124: NullablePrimitive? = NullablePrimitive(124),
        val p125: NullablePrimitive? = NullablePrimitive(125),
        val p126: NullablePrimitive? = NullablePrimitive(126),
        val p127: NullablePrimitive? = NullablePrimitive(127),
        val p128: NullablePrimitive? = NullablePrimitive(128),
        val p129: NullablePrimitive? = NullablePrimitive(129),
        val p130: NullablePrimitive? = NullablePrimitive(130),
        val p131: NullablePrimitive? = NullablePrimitive(131),
        val p132: NullablePrimitive? = NullablePrimitive(132),
        val p133: NullablePrimitive? = NullablePrimitive(133),
        val p134: NullablePrimitive? = NullablePrimitive(134),
        val p135: NullablePrimitive? = NullablePrimitive(135),
        val p136: NullablePrimitive? = NullablePrimitive(136),
        val p137: NullablePrimitive? = NullablePrimitive(137),
        val p138: NullablePrimitive? = NullablePrimitive(138),
        val p139: NullablePrimitive? = NullablePrimitive(139),
        val p140: NullablePrimitive? = NullablePrimitive(140),
        val p141: NullablePrimitive? = NullablePrimitive(141),
        val p142: NullablePrimitive? = NullablePrimitive(142),
        val p143: NullablePrimitive? = NullablePrimitive(143),
        val p144: NullablePrimitive? = NullablePrimitive(144),
        val p145: NullablePrimitive? = NullablePrimitive(145),
        val p146: NullablePrimitive? = NullablePrimitive(146),
        val p147: NullablePrimitive? = NullablePrimitive(147),
        val p148: NullablePrimitive? = NullablePrimitive(148),
        val p149: NullablePrimitive? = NullablePrimitive(149),
        val p150: NullablePrimitive? = NullablePrimitive(150),
        val p151: NullablePrimitive? = NullablePrimitive(151),
        val p152: NullablePrimitive? = NullablePrimitive(152),
        val p153: NullablePrimitive? = NullablePrimitive(153),
        val p154: NullablePrimitive? = NullablePrimitive(154),
        val p155: NullablePrimitive? = NullablePrimitive(155),
        val p156: NullablePrimitive? = NullablePrimitive(156),
        val p157: NullablePrimitive? = NullablePrimitive(157),
        val p158: NullablePrimitive? = NullablePrimitive(158),
        val p159: NullablePrimitive? = NullablePrimitive(159),
        val p160: NullablePrimitive? = NullablePrimitive(160),
        val p161: NullablePrimitive? = NullablePrimitive(161),
        val p162: NullablePrimitive? = NullablePrimitive(162),
        val p163: NullablePrimitive? = NullablePrimitive(163),
        val p164: NullablePrimitive? = NullablePrimitive(164),
        val p165: NullablePrimitive? = NullablePrimitive(165),
        val p166: NullablePrimitive? = NullablePrimitive(166),
        val p167: NullablePrimitive? = NullablePrimitive(167),
        val p168: NullablePrimitive? = NullablePrimitive(168),
        val p169: NullablePrimitive? = NullablePrimitive(169),
        val p170: NullablePrimitive? = NullablePrimitive(170),
        val p171: NullablePrimitive? = NullablePrimitive(171),
        val p172: NullablePrimitive? = NullablePrimitive(172),
        val p173: NullablePrimitive? = NullablePrimitive(173),
        val p174: NullablePrimitive? = NullablePrimitive(174),
        val p175: NullablePrimitive? = NullablePrimitive(175),
        val p176: NullablePrimitive? = NullablePrimitive(176),
        val p177: NullablePrimitive? = NullablePrimitive(177),
        val p178: NullablePrimitive? = NullablePrimitive(178),
        val p179: NullablePrimitive? = NullablePrimitive(179),
        val p180: NullablePrimitive? = NullablePrimitive(180),
        val p181: NullablePrimitive? = NullablePrimitive(181),
        val p182: NullablePrimitive? = NullablePrimitive(182),
        val p183: NullablePrimitive? = NullablePrimitive(183),
        val p184: NullablePrimitive? = NullablePrimitive(184),
        val p185: NullablePrimitive? = NullablePrimitive(185),
        val p186: NullablePrimitive? = NullablePrimitive(186),
        val p187: NullablePrimitive? = NullablePrimitive(187),
        val p188: NullablePrimitive? = NullablePrimitive(188),
        val p189: NullablePrimitive? = NullablePrimitive(189),
        val p190: NullablePrimitive? = NullablePrimitive(190),
        val p191: NullablePrimitive? = NullablePrimitive(191),
        val p192: NullablePrimitive? = NullablePrimitive(192),
        val p193: NullablePrimitive? = NullablePrimitive(193),
        val p194: NullablePrimitive? = NullablePrimitive(194),
        val p195: NullablePrimitive? = NullablePrimitive(195),
        val p196: NullablePrimitive? = NullablePrimitive(196),
        val p197: NullablePrimitive? = NullablePrimitive(197),
        val p198: NullablePrimitive? = NullablePrimitive(198),
        val p199: NullablePrimitive? = NullablePrimitive(199),
        val p200: NullablePrimitive? = NullablePrimitive(200),
        val p201: NullablePrimitive? = NullablePrimitive(201),
        val p202: NullablePrimitive? = NullablePrimitive(202),
        val p203: NullablePrimitive? = NullablePrimitive(203),
        val p204: NullablePrimitive? = NullablePrimitive(204),
        val p205: NullablePrimitive? = NullablePrimitive(205),
        val p206: NullablePrimitive? = NullablePrimitive(206),
        val p207: NullablePrimitive? = NullablePrimitive(207),
        val p208: NullablePrimitive? = NullablePrimitive(208),
        val p209: NullablePrimitive? = NullablePrimitive(209),
        val p210: NullablePrimitive? = NullablePrimitive(210),
        val p211: NullablePrimitive? = NullablePrimitive(211),
        val p212: NullablePrimitive? = NullablePrimitive(212),
        val p213: NullablePrimitive? = NullablePrimitive(213),
        val p214: NullablePrimitive? = NullablePrimitive(214),
        val p215: NullablePrimitive? = NullablePrimitive(215),
        val p216: NullablePrimitive? = NullablePrimitive(216),
        val p217: NullablePrimitive? = NullablePrimitive(217),
        val p218: NullablePrimitive? = NullablePrimitive(218),
        val p219: NullablePrimitive? = NullablePrimitive(219),
        val p220: NullablePrimitive? = NullablePrimitive(220),
        val p221: NullablePrimitive? = NullablePrimitive(221),
        val p222: NullablePrimitive? = NullablePrimitive(222),
        val p223: NullablePrimitive? = NullablePrimitive(223),
        val p224: NullablePrimitive? = NullablePrimitive(224),
        val p225: NullablePrimitive? = NullablePrimitive(225),
        val p226: NullablePrimitive? = NullablePrimitive(226),
        val p227: NullablePrimitive? = NullablePrimitive(227),
        val p228: NullablePrimitive? = NullablePrimitive(228),
        val p229: NullablePrimitive? = NullablePrimitive(229),
        val p230: NullablePrimitive? = NullablePrimitive(230),
        val p231: NullablePrimitive? = NullablePrimitive(231),
        val p232: NullablePrimitive? = NullablePrimitive(232),
        val p233: NullablePrimitive? = NullablePrimitive(233),
        val p234: NullablePrimitive? = NullablePrimitive(234),
        val p235: NullablePrimitive? = NullablePrimitive(235),
        val p236: NullablePrimitive? = NullablePrimitive(236),
        val p237: NullablePrimitive? = NullablePrimitive(237),
        val p238: NullablePrimitive? = NullablePrimitive(238),
        val p239: NullablePrimitive? = NullablePrimitive(239),
        val p240: NullablePrimitive? = NullablePrimitive(240),
        val p241: NullablePrimitive? = NullablePrimitive(241),
        val p242: NullablePrimitive? = NullablePrimitive(242),
        val p243: NullablePrimitive? = NullablePrimitive(243),
        val p244: NullablePrimitive? = NullablePrimitive(244),
    )

    @Test
    fun testMax() {
        assertReflectEquals(DstMax(), defaultMapper.readValue<DstMax>("{}"))
    }
}
