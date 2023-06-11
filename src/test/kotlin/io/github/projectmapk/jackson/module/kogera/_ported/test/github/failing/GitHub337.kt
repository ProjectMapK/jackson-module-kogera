package io.github.projectmapk.jackson.module.kogera._ported.test.github.failing

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY
import io.github.projectmapk.jackson.module.kogera.jacksonMapperBuilder
import io.github.projectmapk.jackson.module.kogera.testPrettyWriter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Fields named "is…" are only serialized if they are Boolean
 */
class TestGitHub337 {
    private val mapper = jacksonMapperBuilder()
        .configure(SORT_PROPERTIES_ALPHABETICALLY, true)
        .build()
        .setSerializationInclusion(JsonInclude.Include.ALWAYS)
    private val writer = mapper.testPrettyWriter()

    @Test
    fun test_ClassWithIsFields() {
        data class ClassWithIsFields(
            val isBooleanField: Boolean,
            val isIntField: Int
        )

        val problematic = ClassWithIsFields(true, 9)
        val expected = """
        {
          "isBooleanField" : true,
          "isIntField" : 9
        }
        """.trimIndent()
        assertEquals(expected, writer.writeValueAsString(problematic))
    }

    @Test
    fun test_AnnotatedClassWithIsFields() {
        data class ClassWithIsFields(
            @JsonProperty("isBooleanField") val isBooleanField: Boolean,
            @JsonProperty("isIntField") val isIntField: Int
        )

        val problematic = ClassWithIsFields(true, 9)
        val expected = """
        {
          "isBooleanField" : true,
          "isIntField" : 9
        }
        """.trimIndent()
        assertEquals(expected, writer.writeValueAsString(problematic))
    }

    @Test
    fun test_AnnotatedGetClassWithIsFields() {
        data class ClassWithIsFields(
            @JsonProperty("isBooleanField") val isBooleanField: Boolean,
            @get:JsonProperty("isIntField") val isIntField: Int
        )

        val problematic = ClassWithIsFields(true, 9)
        val expected = """
        {
          "isBooleanField" : true,
          "isIntField" : 9
        }
        """.trimIndent()
        assertEquals(expected, writer.writeValueAsString(problematic))
    }
}
