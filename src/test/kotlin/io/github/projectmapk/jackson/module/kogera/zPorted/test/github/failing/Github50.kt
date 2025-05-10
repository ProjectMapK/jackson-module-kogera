package io.github.projectmapk.jackson.module.kogera.zPorted.test.github.failing

import com.fasterxml.jackson.annotation.JsonUnwrapped
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
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
        val obj: Employee = jacksonObjectMapper().readValue(json)
        assertEquals(Name("John", "Smith"), obj.name)
        assertEquals("Manager", obj.position)
    }
}
