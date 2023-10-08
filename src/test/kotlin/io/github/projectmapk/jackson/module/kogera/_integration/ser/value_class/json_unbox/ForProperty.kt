package io.github.projectmapk.jackson.module.kogera._integration.ser.value_class.json_unbox

import io.github.projectmapk.jackson.module.kogera.annotation.JsonUnbox
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
        @get:JsonUnbox
        val p0: Primitive = Primitive(0),
        @get:JsonUnbox
        val p1: Primitive? = Primitive(1),
        @get:JsonUnbox
        val p2: Primitive? = null,
        @get:JsonUnbox
        val nno0: NonNullObject = NonNullObject("0"),
        @get:JsonUnbox
        val nno1: NonNullObject? = NonNullObject("1"),
        @get:JsonUnbox
        val nno2: NonNullObject? = null,
        @get:JsonUnbox
        val no0: NullableObject = NullableObject("0"),
        @get:JsonUnbox
        val no1: NullableObject = NullableObject(null),
        @get:JsonUnbox
        val no2: NullableObject? = NullableObject("2"),
        @get:JsonUnbox
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