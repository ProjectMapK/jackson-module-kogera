package com.fasterxml.jackson.module.kotlin._ported.test.github

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.test.github._ported.CloneableJavaObj
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestGithub88 {
    class CloneableKotlinObj(val id: String) : Cloneable

    @Test
    fun shouldDeserializeSuccessfullyKotlinCloneableObject() {
        val result = jacksonObjectMapper().writeValueAsString(CloneableKotlinObj("123"))

        assertEquals("{\"id\":\"123\"}", result)
    }

    @Test
    fun shouldDeserializeSuccessfullyJavaCloneableObject() {
        val result = jacksonObjectMapper().writeValueAsString(
            CloneableJavaObj(
                "123"
            )
        )

        assertEquals("{\"id\":\"123\"}", result)
    }
}
