package com.fasterxml.jackson.module.kotlin._integration.deser.value_class.default_argument

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ConstructorTest {
    data class Dst(val uInt: UInt = UInt.MAX_VALUE)

    @Test
    fun withDefault() {
        val mapper = jacksonObjectMapper()
        val result = mapper.readValue<Dst>("{}")
        assertEquals(Dst(), result)
    }

    @Test
    fun withoutDefault() {
        val mapper = jacksonObjectMapper()

        val value = (Int.MAX_VALUE.toLong() + 1).toUInt()
        val src = mapper.writeValueAsString(Dst(value))

        val result = mapper.readValue<Dst>(src)
        assertEquals(Dst(value), result)
    }
}
