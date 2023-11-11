package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

/**
 * An empty object should be deserialized as *the* Unit instance
 */
class TestGithub196 {
    @Test
    fun testUnitSingletonDeserialization() {
        assertSame(jacksonObjectMapper().readValue<Unit>("{}"), Unit)
    }
}
