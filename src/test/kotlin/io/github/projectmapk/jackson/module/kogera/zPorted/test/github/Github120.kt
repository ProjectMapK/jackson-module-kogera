package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import io.github.projectmapk.jackson.module.kogera.defaultMapper
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
        val foo = Foo(4711L)
        val bar = Bar(foo)
        val asString = defaultMapper.writeValueAsString(bar)
        assertEquals("{\"foo\":4711}", asString)

        val fromString = defaultMapper.readValue(asString, Bar::class.java)
        assertEquals(bar, fromString)
    }
}
