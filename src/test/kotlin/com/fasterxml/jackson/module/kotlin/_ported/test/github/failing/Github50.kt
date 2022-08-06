package com.fasterxml.jackson.module.kotlin._ported.test.github.failing

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException
import com.fasterxml.jackson.module.kotlin._ported.test.expectFailure
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestGithub50 {
    data class Name(val firstName: String, val lastName: String)

    data class Employee(
        @get:JsonUnwrapped val name: Name,
        val position: String
    )

    @Test
    fun testGithub50UnwrappedError() {
        val json = """{"firstName":"John","lastName":"Smith","position":"Manager"}"""
        expectFailure<InvalidDefinitionException>("GitHub #50 has been fixed!") {
            val obj: Employee = jacksonObjectMapper().readValue(json)
            assertEquals(Name("John", "Smith"), obj.name)
            assertEquals("Manager", obj.position)
        }
    }
}
