package com.fasterxml.jackson.module.kotlin._ported.test.github

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

private class TestGithub32 {
    @Test
    fun `valid mandatory data class constructor param`() {
        jacksonObjectMapper().readValue<Person>("""
        {
            "firstName": "James",
            "lastName": "Bond"
        }
        """.trimIndent())
    }

    @Test fun `missing mandatory data class constructor param`() {
        val thrown = assertThrows<MissingKotlinParameterException>(
            "MissingKotlinParameterException with missing `firstName` parameter"
        ) {
            jacksonObjectMapper().readValue<Person>("""
                {
                    "lastName": "Bond"
                }
            """.trimIndent())
        }

        assertEquals(::Person.parameters[0], thrown.parameter)
        assertEquals("firstName", thrown.getHumanReadablePath())
        assertEquals(3, thrown.location?.lineNr)
        assertEquals(1, thrown.location?.columnNr)
    }

    @Test fun `null mandatory data class constructor param`() {
        val thrown = assertThrows<MissingKotlinParameterException> {
            jacksonObjectMapper().readValue<Person>("""
            {
                "firstName": null,
                "lastName": "Bond"
            }
            """.trimIndent())
        }

        assertEquals(::Person.parameters[0], thrown.parameter)
        assertEquals("firstName", thrown.getHumanReadablePath())
        assertEquals(4, thrown.location?.lineNr)
        assertEquals(1, thrown.location?.columnNr)
    }

    @Test fun `missing mandatory constructor param - nested in class with default constructor`() {
        val thrown = assertThrows<MissingKotlinParameterException> {
            jacksonObjectMapper().readValue<WrapperWithDefaultContructor>(
                """
            {
                "person": {
                    "lastName": "Bond"
                }
            }
            """.trimIndent()
            )
        }

        assertEquals(::Person.parameters[0], thrown.parameter)
        assertEquals("person.firstName", thrown.getHumanReadablePath())
        assertEquals(4, thrown.location?.lineNr)
        assertEquals(5, thrown.location?.columnNr)
    }

    @Test fun `missing mandatory constructor param - nested in class with single arg constructor`() {
        val thrown = assertThrows<MissingKotlinParameterException> {
            jacksonObjectMapper().readValue<WrapperWithArgsContructor>(
                """
                {
                    "person": {
                        "lastName": "Bond"
                    }
                }
                """.trimIndent()
            )
        }

        assertEquals(::Person.parameters[0], thrown.parameter)
        assertEquals("person.firstName", thrown.getHumanReadablePath())
        assertEquals(4, thrown.location?.lineNr)
        assertEquals(5, thrown.location?.columnNr)
    }

    @Test fun `missing mandatory constructor param - nested in class with List arg constructor`() {
        val thrown = assertThrows<MissingKotlinParameterException> {
            jacksonObjectMapper().readValue<Crowd>("""
            {
                "people": [
                    {
                        "person": {
                            "lastName": "Bond"
                        }
                    }
                ]
            }
            """.trimIndent())
        }

        assertEquals(::Person.parameters[0], thrown.parameter)
        assertEquals("people[0].firstName", thrown.getHumanReadablePath())
        assertEquals(7, thrown.location?.lineNr)
        assertEquals(9, thrown.location?.columnNr)
    }

}


private data class Person(val firstName: String, val lastName: String)

private data class WrapperWithArgsContructor(val person: Person)

private data class WrapperWithDefaultContructor(val person: Person? = null)

private data class Crowd(val people: List<Person>)


// private fun missingFirstNameParameter() = missingConstructorParam(::Person.parameters[0])

// private fun missingConstructorParam(param: KParameter) = object : CustomTypeSafeMatcher<MissingKotlinParameterException>("MissingKotlinParameterException with missing `${param.name}` parameter") {
//     override fun matchesSafely(e: MissingKotlinParameterException): Boolean = e.parameter.equals(param)
// }
//
// private fun pathMatches(path: String) = object : CustomTypeSafeMatcher<MissingKotlinParameterException>("MissingKotlinParameterException with path `$path`") {
//     override fun matchesSafely(e: MissingKotlinParameterException): Boolean = e.getHumanReadablePath().equals(path)
// }
//
// private fun location(line: Int, column: Int) = object : CustomTypeSafeMatcher<MissingKotlinParameterException>("MissingKotlinParameterException with location (line=$line, column=$column)") {
//     override fun matchesSafely(e: MissingKotlinParameterException): Boolean {
//         return e.location != null && line.equals(e.location.lineNr) && column.equals(e.location.columnNr)
//     }
// }

private fun JsonMappingException.getHumanReadablePath(): String {
    val builder = StringBuilder()
    this.path.forEachIndexed { i, reference ->
        if (reference.index >= 0) {
            builder.append("[${reference.index}]")
        } else {
            if (i > 0) builder.append(".")
            builder.append(reference.fieldName)
        }
    }
    return builder.toString()
}
