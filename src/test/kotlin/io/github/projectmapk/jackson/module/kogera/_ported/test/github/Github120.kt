package io.github.projectmapk.jackson.module.kogera._ported.test.github

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestGithub120 {
    data class Foo
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    constructor(
        @JsonValue
        val value: Long
    )

    data class Bar(
        val foo: Foo
    )

    @Test
    fun testNestedJsonValue() {
        val om = jacksonObjectMapper()
        val foo = Foo(4711L)
        val bar = Bar(foo)
        val asString = om.writeValueAsString(bar)
        assertEquals("{\"foo\":4711}", asString)

        val fromString = om.readValue(asString, Bar::class.java)
        assertEquals(bar, fromString)
    }
}
