package io.github.projectmapk.jackson.module.kogera

import com.fasterxml.jackson.annotation.JsonProperty
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

private data class Dto(
    val foo: String,
    @JsonProperty("bar")
    val _bar: String,
)

class KotlinInvalidNullExceptionTest {
    @Test
    fun fooTest() {
        val json = """{"bar":"bar"}"""
        val ex = assertThrows<KotlinInvalidNullException> { defaultMapper.readValue<Dto>(json) }

        assertEquals("foo", ex.kotlinPropertyName)
        assertEquals("foo", ex.propertyName.simpleName)
        assertEquals(Dto::class, ex.targetType.kotlin)
    }

    @Test
    fun barTest() {
        val json = """{"foo":"foo","bar":null}"""
        val ex = assertThrows<KotlinInvalidNullException> { defaultMapper.readValue<Dto>(json) }

        assertEquals("_bar", ex.kotlinPropertyName)
        assertEquals("bar", ex.propertyName.simpleName)
        assertEquals(Dto::class, ex.targetType.kotlin)
    }
}
