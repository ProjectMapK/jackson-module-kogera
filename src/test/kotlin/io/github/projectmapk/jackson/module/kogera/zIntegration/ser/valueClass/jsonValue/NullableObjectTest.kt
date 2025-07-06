package io.github.projectmapk.jackson.module.kogera.zIntegration.ser.valueClass.jsonValue

import com.fasterxml.jackson.annotation.JsonValue
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class NullableObjectTest {
    @JvmInline
    value class Value(val v: String?) {
        @JsonValue
        fun jsonValue() = v?.let { it + "_modified" }
    }

    @Nested
    inner class DirectTest {
        @Test
        fun nonNull() {
            assertEquals(
                "\"test_modified\"",
                defaultMapper.writeValueAsString(Value("test")),
            )
        }

        @Test
        fun `null`() {
            assertEquals(
                "null",
                defaultMapper.writeValueAsString(Value(null)),
            )
        }
    }

    data class Dto(val v: Value)

    @Nested
    inner class AsPropertyTest {
        @Test
        fun nonNull() {
            assertEquals(
                """{"v":"test_modified"}""",
                defaultMapper.writeValueAsString(Dto(Value("test"))),
            )
        }

        @Test
        fun `null`() {
            assertEquals(
                """{"v":null}""",
                defaultMapper.writeValueAsString(Dto(Value(null))),
            )
        }
    }
}
