package io.github.projectmapk.jackson.module.kogera._ported.test.github.failing

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.projectmapk.jackson.module.kogera.kotlinModule
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class OwnerRequestTest {
    private val jackson = ObjectMapper().registerModule(kotlinModule())

    val json = """{"foo": "Got a foo"}"""

    class NoIsField(val foo: String? = null)

    class IsField(val foo: String? = null) {
        val isFoo = foo != null
    }

    @Test
    fun testDeserHit340() {
        val value: IsField = jackson.readValue(json)
        assertEquals("Got a foo", value.foo)
    }

    @Test
    fun testDeserWithoutIssue() {
        val value: NoIsField = jackson.readValue(json)
        assertEquals("Got a foo", value.foo)
    }
}
