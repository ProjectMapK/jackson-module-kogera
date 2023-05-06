package io.github.projectmapk.jackson.module.kogera._ported.test.github.failing

import io.github.projectmapk.jackson.module.kogera._ported.test.expectFailure
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestGithub71 {
    open class Identifiable {
        internal var identity: Long? = null
    }

    @Test
    fun testInternalPropertySerliazation() {
        val json = jacksonObjectMapper().writeValueAsString(Identifiable())

        expectFailure<AssertionError>("GitHub #71 has been fixed!") {
            assertEquals("{\"identity\":null}", json) // fails: {"identity$jackson_module_kotlin":null}
            val newInstance = jacksonObjectMapper().readValue<Identifiable>(json)
            assertEquals(Identifiable(), newInstance)
        }
    }
}
