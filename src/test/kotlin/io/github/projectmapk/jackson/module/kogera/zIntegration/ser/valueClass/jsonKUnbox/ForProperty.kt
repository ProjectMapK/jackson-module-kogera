package io.github.projectmapk.jackson.module.kogera.zIntegration.ser.valueClass.jsonKUnbox

import io.github.projectmapk.jackson.module.kogera.annotation.JsonKUnbox
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ForProperty {
    @JvmInline
    value class Primitive(val v: Int)

    @JvmInline
    value class NonNullObject(val v: String)

    @JvmInline
    value class NullableObject(val v: String?)

    @JvmInline
    value class NullablePrimitive(val v: Int?)

    @JvmInline
    value class TwoUnitPrimitive(val v: Long)

    data class Dto(
        @get:JsonKUnbox
        val p0: Primitive = Primitive(0),
        @get:JsonKUnbox
        val p1: Primitive? = Primitive(1),
        @get:JsonKUnbox
        val p2: Primitive? = null,
        @get:JsonKUnbox
        val nno0: NonNullObject = NonNullObject("0"),
        @get:JsonKUnbox
        val nno1: NonNullObject? = NonNullObject("1"),
        @get:JsonKUnbox
        val nno2: NonNullObject? = null,
        @get:JsonKUnbox
        val no0: NullableObject = NullableObject("0"),
        @get:JsonKUnbox
        val no1: NullableObject = NullableObject(null),
        @get:JsonKUnbox
        val no2: NullableObject? = NullableObject("2"),
        @get:JsonKUnbox
        val no3: NullableObject? = null,
        @get:JsonKUnbox
        val np0: NullablePrimitive = NullablePrimitive(0),
        @get:JsonKUnbox
        val np1: NullablePrimitive = NullablePrimitive(null),
        @get:JsonKUnbox
        val np2: NullablePrimitive? = NullablePrimitive(2),
        @get:JsonKUnbox
        val np3: NullablePrimitive? = null,
        @get:JsonKUnbox
        val tup0: TwoUnitPrimitive = TwoUnitPrimitive(0),
        @get:JsonKUnbox
        val tup1: TwoUnitPrimitive? = TwoUnitPrimitive(1),
        @get:JsonKUnbox
        val tup2: TwoUnitPrimitive? = null,
    )

    @Test
    fun test() {
        val expected = """
            {"p0":0,"p1":1,"p2":null,"nno0":"0","nno1":"1","nno2":null,"no0":"0","no1":null,"no2":"2","no3":null,"np0":0,"np1":null,"np2":2,"np3":null,"tup0":0,"tup1":1,"tup2":null}
        """.trimIndent()
        val actual = defaultMapper.writeValueAsString(Dto())

        assertEquals(expected, actual)
    }
}
