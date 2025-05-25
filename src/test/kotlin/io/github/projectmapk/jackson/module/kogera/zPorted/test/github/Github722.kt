package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.InjectableValues
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private class Github722 {
    data class FailingDto @JsonCreator constructor(
        @JacksonInject("foo")
        @JsonProperty("foo")
        val foo: Int = 100,
        @JacksonInject("bar")
        @JsonProperty("bar")
        val bar: Int? = 200
    )

    val injectValues = mapOf("foo" to 1, "bar" to 2)
    val expected = FailingDto(1, 2)

    @Test
    fun onPlainMapper() {
        // Succeeds in plain mapper
        val plainMapper = ObjectMapper()
        assertEquals(
            expected,
            plainMapper.readerFor(FailingDto::class.java)
                .with(InjectableValues.Std(injectValues))
                .readValue("{}")
        )
    }

    @Test
    fun failing() {
        // The kotlin mapper uses the Kotlin default value instead of the Inject value.
        val reader = jacksonObjectMapper()
            .readerFor(FailingDto::class.java)
            .with(InjectableValues.Std(injectValues))
        val result = reader.readValue<FailingDto>("{}")

        // fixed
        // assertNotEquals(result, expected, "GitHubXXX fixed.")
        assertEquals(expected, result)
    }

    data class WithoutDefaultValue(
        @JacksonInject("foo")
        val foo: Int,
        @JacksonInject("bar")
        val bar: Int?
    )

    @Test
    fun withoutDefaultValue() {
        val reader = jacksonObjectMapper()
            .readerFor(WithoutDefaultValue::class.java)
            .with(InjectableValues.Std(injectValues))
        val result = reader.readValue<WithoutDefaultValue>("{}")

        // If there is no default value, the problem does not occur.
        assertEquals(WithoutDefaultValue(1, 2), result)
    }
}
