package io.github.projectmapk.jackson.module.kogera.zIntegration.ser.valueClass.jsonKUnbox

import io.github.projectmapk.jackson.module.kogera.annotation.JsonKUnbox
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ForClass {
    @JvmInline
    @JsonKUnbox
    value class Primitive(val v: Int)

    @JvmInline
    @JsonKUnbox
    value class NonNullObject(val v: String)

    @JvmInline
    @JsonKUnbox
    value class NullableObject(val v: String?)

    @JsonKUnbox
    @JvmInline
    value class TwoUnitPrimitive(val v: Long)

    data class Dto(
        val p0: Primitive = Primitive(0),
        val p1: Primitive? = Primitive(1),
        val p2: Primitive? = null,
        val nno0: NonNullObject = NonNullObject("0"),
        val nno1: NonNullObject? = NonNullObject("1"),
        val nno2: NonNullObject? = null,
        val no0: NullableObject = NullableObject("0"),
        val no1: NullableObject = NullableObject(null),
        val no2: NullableObject? = NullableObject("2"),
        val no3: NullableObject? = null,
        val tup0: TwoUnitPrimitive = TwoUnitPrimitive(0),
        val tup1: TwoUnitPrimitive? = TwoUnitPrimitive(1),
        val tup2: TwoUnitPrimitive? = null
    )

    @Test
    fun test() {
        val expected = """
            {"p0":0,"p1":1,"p2":null,"nno0":"0","nno1":"1","nno2":null,"no0":"0","no1":null,"no2":"2","no3":null,"tup0":0,"tup1":1,"tup2":null}
        """.trimIndent()
        val actual = jacksonObjectMapper().writeValueAsString(Dto())

        assertEquals(expected, actual)
    }
}
