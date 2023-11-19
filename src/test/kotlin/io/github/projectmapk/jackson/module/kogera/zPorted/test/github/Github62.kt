package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestGithub62 {
    @Test
    fun testAnonymousClassSerialization() {
        val externalValue = "ggg"

        val result = jacksonObjectMapper().writeValueAsString(object {
            val value = externalValue
        })

        assertEquals("""{"value":"ggg"}""", result)
    }
}
