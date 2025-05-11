package io.github.projectmapk.jackson.module.kogera.zPorted.test.github.failing

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.zPorted.test.expectFailure
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestGithub271 {
    @JsonPropertyOrder(alphabetic = true)
    data class Foo(val a: String, val c: String) {
        val b = "b"
    }

    @Test
    fun testAlphabeticFields() {
        val json = defaultMapper.writeValueAsString(Foo("a", "c"))
        expectFailure<AssertionError>("GitHub #271 has been fixed!") {
            assertEquals("""{"a":"a","b":"b","c":"c"}""", json)
        }
    }
}
