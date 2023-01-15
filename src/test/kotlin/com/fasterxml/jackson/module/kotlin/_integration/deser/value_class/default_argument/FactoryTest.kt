package com.fasterxml.jackson.module.kotlin._integration.deser.value_class.default_argument

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FactoryTest {
    data class Dst(val uInt: UInt) {
        companion object {
            @JvmStatic
            @JsonCreator
            fun creator(uInt: UInt = UInt.MAX_VALUE): Dst = Dst(uInt)
        }
    }

    @Test
    fun withDefault() {
        val mapper = jacksonObjectMapper()
        val result = mapper.readValue<Dst>("{}")
        Assertions.assertEquals(Dst.creator(), result)
    }

    @Test
    fun withoutDefault() {
        val mapper = jacksonObjectMapper()

        val value = (Int.MAX_VALUE.toLong() + 1).toUInt()
        val src = mapper.writeValueAsString(Dst(value))

        val result = mapper.readValue<Dst>(src)
        Assertions.assertEquals(Dst(value), result)
    }
}
