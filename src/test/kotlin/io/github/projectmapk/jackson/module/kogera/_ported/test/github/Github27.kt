package io.github.projectmapk.jackson.module.kogera._ported.test.github

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.github.projectmapk.jackson.module.kogera._ported.test.expectFailure
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class TestGithub27 {
    val mapper: ObjectMapper = jacksonObjectMapper()
        .configure(SerializationFeature.INDENT_OUTPUT, false)

    private data class ClassWithNullableInt(val sample: Int?)

    @Test
    fun testNullableInt() {
        val json = """{"sample":null}"""
        val stateObj = mapper.readValue<ClassWithNullableInt>(json)
        assertEquals(ClassWithNullableInt(null), stateObj)
    }

    private data class ClassWithInt(val sample: Int)

    @Test fun testInt() {
        val json = """{"sample":null}"""
        val stateObj = mapper.readValue<ClassWithInt>(json)
        assertEquals(ClassWithInt(0), stateObj)
    }

    private data class ClassWithListOfNullableInt(val samples: List<Int?>)

    @Test fun testListOfNullableInt() {
        val json = """{"samples":[1, null]}"""
        val stateObj = mapper.readValue<ClassWithListOfInt>(json)
        assertEquals(listOf(1, null), stateObj.samples)
    }

    private data class ClassWithListOfInt(val samples: List<Int>)

    // Would be hard to look into generics of every possible type of collection or generic object to check nullability of each item, maybe only possible for simple known collections
    @Test
    fun testListOfInt() {
        val json = """{"samples":[1, null]}"""
        val stateObj = mapper.readValue<ClassWithListOfInt>(json)
        expectFailure<NullPointerException>("Problem with nullable generics related to #27 has been fixed!") {
            assertTrue(
                stateObj.samples.none {
                    @Suppress("SENSELESS_COMPARISON")
                    (it == null)
                }
            )
            fail("")
        }
    }

    // work around to above
    private class ClassWithListOfIntProtected(val samples: List<Int>) {
        @get:JsonIgnore val safeSamples: List<Int> by lazy { samples.filterNotNull() }
    }

    // TODO:  this would get tougher to nullable check, tough problem to solve
    class ClassWithNonNullableT<T>(
        val something: T,
        val listSomething: List<T>,
        val mapOfListOfSomething: Map<String, List<T>>,
        val innerSomething: ClassWithNonNullableT<T>?
    )
}
