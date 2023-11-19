package io.github.projectmapk.jackson.module.kogera.zPorted.test

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.projectmapk.jackson.module.kogera.KotlinFeature.NullToEmptyCollection
import io.github.projectmapk.jackson.module.kogera.kotlinModule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestNullToEmptyCollection {

    private data class TestClass(val foo: List<Int>)

    @Test
    fun nonNullCaseStillWorks() {
        val mapper = createMapper()
        assertEquals(listOf(1, 2), mapper.readValue("""{"foo": [1,2]}""", TestClass::class.java).foo)
    }

    @Test
    fun shouldMapNullValuesToEmpty() {
        val mapper = createMapper()
        assertEquals(emptyList<Int>(), mapper.readValue("{}", TestClass::class.java).foo)
        assertEquals(emptyList<Int>(), mapper.readValue("""{"foo": null}""", TestClass::class.java).foo)
    }

    private fun createMapper(): ObjectMapper {
        return ObjectMapper().registerModule(
            kotlinModule {
                enable(
                    NullToEmptyCollection
                )
            }
        )
    }
}
