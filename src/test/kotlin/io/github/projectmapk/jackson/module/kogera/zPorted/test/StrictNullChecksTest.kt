package io.github.projectmapk.jackson.module.kogera.zPorted.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.InvalidNullException
import io.github.projectmapk.jackson.module.kogera.KotlinFeature.StrictNullChecks
import io.github.projectmapk.jackson.module.kogera.kotlinModule
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class StrictNullChecksTest {
    private val mapper = ObjectMapper().registerModule(
        kotlinModule {
            enable(
                StrictNullChecks
            )
        }
    )

    /** collection tests */

    private data class ClassWithListOfNullableInt(val samples: List<Int?>)

    @Test
    fun testListOfNullableInt() {
        val json = """{"samples":[1, null]}"""
        val stateObj = mapper.readValue<ClassWithListOfNullableInt>(json)
        assertEquals(listOf(1, null), stateObj.samples)
    }

    private data class ClassWithListOfInt(val samples: List<Int>)

    @Test
    fun testListOfInt() {
        val json = """{"samples":[1, null]}"""
        assertThrows<InvalidNullException> { mapper.readValue<ClassWithListOfInt>(json) }
    }

    private data class ClassWithNullableListOfInt(val samples: List<Int>?)

    @Test
    fun testNullableListOfInt() {
        val json = """{"samples": null}"""
        val stateObj = mapper.readValue<ClassWithNullableListOfInt>(json)
        assertNull(stateObj.samples)
    }

    /** array tests */

    private data class ClassWithArrayOfNullableInt(val samples: Array<Int?>)

    @Test
    fun testArrayOfNullableInt() {
        val json = """{"samples":[1, null]}"""
        val stateObj = mapper.readValue<ClassWithArrayOfNullableInt>(json)
        assertArrayEquals(arrayOf(1, null), stateObj.samples)
    }

    private data class ClassWithArrayOfInt(val samples: Array<Int>)

    @Test
    fun testArrayOfInt() {
        val json = """{"samples":[1, null]}"""
        assertThrows<InvalidNullException> { mapper.readValue<ClassWithArrayOfInt>(json) }
    }

    private data class ClassWithNullableArrayOfInt(val samples: Array<Int>?)

    @Test
    fun testNullableArrayOfInt() {
        val json = """{"samples": null}"""
        val stateObj = mapper.readValue<ClassWithNullableArrayOfInt>(json)
        assertNull(stateObj.samples)
    }

    /** map tests */

    private data class ClassWithMapOfStringToNullableInt(val samples: Map<String, Int?>)

    @Test
    fun testMapOfStringToNullableInt() {
        val json = """{ "samples": { "key": null } }"""
        val stateObj = mapper.readValue<ClassWithMapOfStringToNullableInt>(json)
        assertEquals(mapOf<String, Int?>("key" to null), stateObj.samples)
    }

    private data class ClassWithMapOfStringToInt(val samples: Map<String, Int>)

    @Test
    fun testMapOfStringToIntWithNullValue() {
        val json = """{ "samples": { "key": null } }"""
        assertThrows<InvalidNullException> { mapper.readValue<ClassWithMapOfStringToInt>(json) }
    }

    private data class ClassWithNullableMapOfStringToInt(val samples: Map<String, Int>?)

    @Test
    fun testNullableMapOfStringToInt() {
        val json = """{"samples": null}"""
        val stateObj = mapper.readValue<ClassWithNullableMapOfStringToInt>(json)
        assertNull(stateObj.samples)
    }

    /** generics test */

    private data class TestClass<T>(val samples: T)

    @Test
    fun testListOfGeneric() {
        val json = """{"samples":[1, 2]}"""
        val stateObj = mapper.readValue<TestClass<List<Int>>>(json)
        assertEquals(listOf(1, 2), stateObj.samples)
    }

    // this is a hard problem to solve and is currently not addressed
    @Test
    fun testListOfGenericWithNullValueFailing() {
        val json = """{"samples":[1, null]}"""
        assertDoesNotThrow { mapper.readValue<TestClass<List<Int>>>(json) }
    }

    @Test
    fun testMapOfGeneric() {
        val json = """{ "samples": { "key": 1 } }"""
        val stateObj = mapper.readValue<TestClass<Map<String, Int>>>(json)
        assertEquals(mapOf("key" to 1), stateObj.samples)
    }

    // this is a hard problem to solve and is currently not addressed
    @Test
    fun testMapOfGenericWithNullValueFailing() {
        val json = """{ "samples": { "key": null } }"""
        assertDoesNotThrow { mapper.readValue<TestClass<Map<String, Int>>>(json) }
    }

    @Test
    fun testArrayOfGeneric() {
        val json = """{"samples":[1, 2]}"""
        val stateObj = mapper.readValue<TestClass<Array<Int>>>(json)
        assertArrayEquals(arrayOf(1, 2), stateObj.samples)
    }

    // this is a hard problem to solve and is currently not addressed
    @Test
    fun testArrayOfGenericWithNullValueFailing() {
        val json = """{"samples":[1, null]}"""
        assertDoesNotThrow { mapper.readValue<TestClass<Array<Int>>>(json) }
    }
}
