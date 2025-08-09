package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.projectmapk.jackson.module.kogera.KotlinInvalidNullException
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TestGithub168 {
    @Suppress("UNUSED_PARAMETER")
    class TestClass(@JsonProperty(value = "foo", required = true) foo: String?, val baz: String)

    @Test
    fun testIfRequiredIsReallyRequiredWhenNullUsed() {
        val obj = defaultMapper.readValue<TestClass>("""{"foo":null,"baz":"whatever"}""")
        assertEquals("whatever", obj.baz)
    }

    @Test
    fun testIfRequiredIsReallyRequiredWhenAbsent() {
        assertThrows<KotlinInvalidNullException> {
            defaultMapper.readValue<TestClass>("""{"baz":"whatever"}""")
        }
    }

    @Test
    fun testIfRequiredIsReallyRequiredWhenValuePresent() {
        val obj = defaultMapper.readValue<TestClass>("""{"foo":"yay!","baz":"whatever"}""")
        assertEquals("whatever", obj.baz)
    }
}
