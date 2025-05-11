package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestGithub269 {
    data class Foo(val pattern: Regex)
    data class Bar(val thing: Regex)

    data class Goo(
        @JsonSerialize(using = ToStringSerializer::class)
        val myPattern: Regex
    ) {
        constructor(strPattern: String) : this(Regex(strPattern))
    }

    data class Zoo(
        @JsonSerialize(using = ToStringSerializer::class)
        val myPattern: Regex
    ) {
        @JsonCreator
        constructor(strPattern: String) : this(Regex(strPattern))
    }

    @Test
    fun testGithub269WithFoo() {
        val testObject = Foo(Regex("test"))
        val testJson = defaultMapper.writeValueAsString(testObject)
        val resultObject = defaultMapper.readValue<Foo>(testJson)

        assertEquals(testObject.pattern.pattern, resultObject.pattern.pattern)
        assertEquals(testObject.pattern.options, resultObject.pattern.options)

        defaultMapper.readValue<Foo>("""{"pattern":"test"}""")
    }

    @Test
    fun testGithub269WithBar() {
        val testObject = Bar(Regex("test"))
        val testJson = defaultMapper.writeValueAsString(testObject)
        val resultObject = defaultMapper.readValue<Bar>(testJson)

        assertEquals(testObject.thing.pattern, resultObject.thing.pattern)
        assertEquals(testObject.thing.options, resultObject.thing.options)

        defaultMapper.readValue<Bar>("""{"thing":"test"}""")
    }

    @Test
    fun testGithub269WithGoo() {
        val testObject = Goo(Regex("test_pattern_1"))
        val testJson = defaultMapper.writeValueAsString(testObject)
        val resultObject = defaultMapper.readValue<Goo>(testJson)

        assertEquals(testObject.myPattern.pattern, resultObject.myPattern.pattern)
    }

    @Test
    fun testGithub269WithZoo() {
        val testObject = Zoo(Regex("test_pattern_1"))
        val testJson = defaultMapper.writeValueAsString(testObject)
        val resultObject = defaultMapper.readValue<Zoo>(testJson)

        assertEquals(testObject.myPattern.pattern, resultObject.myPattern.pattern)
    }
}
