package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import io.github.projectmapk.jackson.module.kogera.configOverride
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class GitHub876 {
    data class WithAnnotationWithoutDefault(
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        val list: List<String>,
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        val map: Map<String, String>,
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        val string: String
    )

    @Nested
    inner class WithAnnotationWithoutDefaultTest {
        @Test
        fun nullInput() {
            val input = """{"list": null, "map": null, "string": null}"""
            val expected = WithAnnotationWithoutDefault(emptyList(), emptyMap(), "")

            val actual = defaultMapper.readValue<WithAnnotationWithoutDefault>(input)

            assertEquals(expected, actual)
        }

        @Test
        fun undefinedInput() {
            val input = """{}"""
            val expected = WithAnnotationWithoutDefault(emptyList(), emptyMap(), "")

            val actual = defaultMapper.readValue<WithAnnotationWithoutDefault>(input)

            assertEquals(expected, actual)
        }
    }

    data class WithAnnotationWithDefault(
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        val list: List<String> = listOf("default"),
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        val map: Map<String, String> = mapOf("default" to "default"),
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        val string: String = "default"
    )

    @Nested
    inner class WithAnnotationWithDefaultTest {
        @Test
        fun nullInput() {
            // If null is explicitly specified, the default value is not used
            val input = """{"list": null, "map": null, "string": null}"""
            val expected = WithAnnotationWithDefault(emptyList(), emptyMap(), "")

            val actual = defaultMapper.readValue<WithAnnotationWithDefault>(input)

            assertEquals(expected, actual)
        }

        @Test
        fun undefinedInput() {
            // If the input is undefined, the default value is used
            val input = """{}"""
            val expected = WithAnnotationWithDefault()

            val actual = defaultMapper.readValue<WithAnnotationWithDefault>(input)

            assertEquals(expected, actual)
        }
    }

    // If it is set by configOverride, it is treated in the same way as if it were set by annotation
    data class WithoutAnnotationWithoutDefault(
        val list: List<String>,
        val map: Map<String, String>,
        val string: String
    )

    @Nested
    inner class WithoutAnnotationWithoutDefaultTest {
        val mapper = jacksonObjectMapper().apply {
            configOverride<List<*>>().setterInfo = JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY)
            configOverride<Map<*, *>>().setterInfo = JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY)
            configOverride<String>().setterInfo = JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY)
        }

        @Test
        fun nullInput() {
            val input = """{"list": null, "map": null, "string": null}"""
            val expected = WithoutAnnotationWithoutDefault(emptyList(), emptyMap(), "")

            val actual = mapper.readValue<WithoutAnnotationWithoutDefault>(input)

            assertEquals(expected, actual)
        }

        @Test
        fun undefinedInput() {
            val input = """{}"""
            val expected = WithoutAnnotationWithoutDefault(emptyList(), emptyMap(), "")

            val actual = mapper.readValue<WithoutAnnotationWithoutDefault>(input)

            assertEquals(expected, actual)
        }
    }

    data class WithoutAnnotationWithDefault(
        val list: List<String> = listOf("default"),
        val map: Map<String, String> = mapOf("default" to "default"),
        val string: String = "default"
    )

    @Nested
    inner class WithoutAnnotationWithDefaultTest {
        val mapper = jacksonObjectMapper().apply {
            configOverride<List<*>>().setterInfo = JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY)
            configOverride<Map<*, *>>().setterInfo = JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY)
            configOverride<String>().setterInfo = JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY)
        }

        @Test
        fun nullInput() {
            val input = """{"list": null, "map": null, "string": null}"""
            val expected = WithoutAnnotationWithDefault(emptyList(), emptyMap(), "")

            val actual = mapper.readValue<WithoutAnnotationWithDefault>(input)

            assertEquals(expected, actual)
        }

        @Test
        fun undefinedInput() {
            val input = """{}"""
            val expected = WithoutAnnotationWithDefault()

            val actual = mapper.readValue<WithoutAnnotationWithDefault>(input)

            assertEquals(expected, actual)
        }
    }
}
