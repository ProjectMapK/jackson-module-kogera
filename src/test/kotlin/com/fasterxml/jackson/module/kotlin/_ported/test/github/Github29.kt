package com.fasterxml.jackson.module.kotlin._ported.test.github

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestGithub29 {
    data class Github29TestObj(val name: String, val other: String = "test")

    @Test
    fun testDefaultValuesInDeser() {
        val check1: Github29TestObj = jacksonObjectMapper().readValue("""{"name": "bla"}""")
        assertEquals("bla", check1.name)
        assertEquals("test", check1.other)

        val check2: Github29TestObj = jacksonObjectMapper().readValue("""{"name": "bla", "other": "fish"}""")
        assertEquals("bla", check2.name)
        assertEquals("fish", check2.other)
    }
}
