package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestGithub80 {
    @Test
    fun testIsBool() {
        val example = IsBoolExample(true)
        val json = defaultMapper.writeValueAsString(example)
        assertEquals("{\"isTrueOrFalse\":true}", json)

        val deserialized = defaultMapper.readValue(json, IsBoolExample::class.java)
        assertEquals(example.isTrueOrFalse, deserialized.isTrueOrFalse)
    }

    @Test
    fun testAnnotatedIsBool() {
        val example = IsBoolAnnotatedExample(true)
        val json = defaultMapper.writeValueAsString(example)
        assertEquals("{\"isTrueOrFalse\":true}", json)

        val deserialized = defaultMapper.readValue(json, IsBoolAnnotatedExample::class.java)
        assertEquals(example.isTrue, deserialized.isTrue)
    }

    class IsBoolExample(val isTrueOrFalse: Boolean)

    class IsBoolAnnotatedExample(@JsonProperty("isTrueOrFalse") val isTrue: Boolean)
}
