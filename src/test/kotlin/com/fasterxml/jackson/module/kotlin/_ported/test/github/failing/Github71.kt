package com.fasterxml.jackson.module.kotlin._ported.test.github.failing

import com.fasterxml.jackson.module.kotlin._ported.test.expectFailure
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
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
