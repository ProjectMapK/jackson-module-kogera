package io.github.projectmapk.jackson.module.kogera._ported.test.github

import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.test.github._ported.CloneableJavaObj
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestGithub88 {
    class CloneableKotlinObj(val id: String) : Cloneable

    @Test
    fun shouldDeserializeSuccessfullyKotlinCloneableObject() {
        val result = io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper().writeValueAsString(CloneableKotlinObj("123"))

        assertEquals("{\"id\":\"123\"}", result)
    }

    @Test
    fun shouldDeserializeSuccessfullyJavaCloneableObject() {
        val result = io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper().writeValueAsString(
            io.github.projectmapk.jackson.module.kogera.test.github._ported.CloneableJavaObj(
                "123"
            )
        )

        assertEquals("{\"id\":\"123\"}", result)
    }
}
