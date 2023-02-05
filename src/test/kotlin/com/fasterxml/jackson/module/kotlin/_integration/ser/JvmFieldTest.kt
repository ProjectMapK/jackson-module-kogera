package com.fasterxml.jackson.module.kotlin._integration.ser

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class JvmFieldTest {
    data class Src(
        @JvmField
        val `foo-foo`: String,
        @JvmField
        val `-bar`: String
    )

    @Test
    fun test() {
        val mapper = jacksonObjectMapper()
        val r = mapper.writeValueAsString(Src("foo", "bar"))

        assertEquals("{\"foo-foo\":\"foo\",\"-bar\":\"bar\"}", r)
    }
}
