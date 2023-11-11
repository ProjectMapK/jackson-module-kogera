package io.github.projectmapk.jackson.module.kogera.zIntegration.ser.valueClass.jsonKUnbox

import io.github.projectmapk.jackson.module.kogera.annotation.JsonKUnbox
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ForProperty {
    @JvmInline
    value class Primitive(val v: Int)

    @JvmInline
    value class NonNullObject(val v: String)

    @JvmInline
    value class NullableObject(val v: String?)

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
        val no3: NullableObject? = null
    )

    @Test
    fun test() {
        val expected =
            """{"p0":0,"p1":1,"p2":null,"nno0":"0","nno1":"1","nno2":null,"no0":"0","no1":null,"no2":"2","no3":null}"""
        val actual = jacksonObjectMapper().writeValueAsString(Dto())

        assertEquals(expected, actual)
    }
}
