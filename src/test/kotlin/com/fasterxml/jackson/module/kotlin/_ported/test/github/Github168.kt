package com.fasterxml.jackson.module.kotlin._ported.test.github

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TestGithub168 {
    @Suppress("UNUSED_PARAMETER")
    class TestClass(@JsonProperty(value = "foo", required = true) foo: String?, val baz: String)

    @Test
    fun testIfRequiredIsReallyRequiredWhenNullused() {
        val obj = jacksonObjectMapper().readValue<TestClass>("""{"foo":null,"baz":"whatever"}""")
        assertEquals("whatever", obj.baz)
    }

    @Test
    fun testIfRequiredIsReallyRequiredWhenAbsent() {
        assertThrows<MismatchedInputException> {
            jacksonObjectMapper().readValue<TestClass>("""{"baz":"whatever"}""")
        }
    }

    @Test
    fun testIfRequiredIsReallyRequiredWhenVauePresent() {
        val obj = jacksonObjectMapper().readValue<TestClass>("""{"foo":"yay!","baz":"whatever"}""")
        assertEquals("whatever", obj.baz)
    }
}
