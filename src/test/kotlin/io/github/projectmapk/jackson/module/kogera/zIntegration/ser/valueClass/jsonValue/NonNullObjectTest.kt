package io.github.projectmapk.jackson.module.kogera.zIntegration.ser.valueClass.jsonValue

import com.fasterxml.jackson.annotation.JsonValue
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class NonNullObjectTest {
    @JvmInline
    value class NonNull(val v: String) {
        @JsonValue
        fun jsonValue() = v + "_modified"
    }

    data class NonNullDto(val v: NonNull)

    @Nested
    inner class NonNullTest {
        @Test
        fun direct() {
            assertEquals(
                "\"test_modified\"",
                defaultMapper.writeValueAsString(NonNull("test")),
            )
        }

        @Test
        fun asProperty() {
            assertEquals(
                """{"v":"test_modified"}""",
                defaultMapper.writeValueAsString(NonNullDto(NonNull("test"))),
            )
        }
    }

    @JvmInline
    value class Nullable(val v: String) {
        @JsonValue
        fun jsonValue() = v.takeIf { it.length % 2 == 0 }?.let { it + "_modified" }
    }

    data class NullableDto(val v: Nullable)

    @Nested
    inner class NullableTest {
        @Nested
        inner class DirectTest {
            @Test
            fun nonNull() {
                assertEquals(
                    "\"even_modified\"",
                    defaultMapper.writeValueAsString(Nullable("even")),
                )
            }

            @Test
            fun `null`() {
                assertEquals(
                    "null",
                    defaultMapper.writeValueAsString(Nullable("odd")),
                )
            }
        }

        @Nested
        inner class AsPropertyTest {
            @Test
            fun nonNull() {
                assertEquals(
                    """{"v":"even_modified"}""",
                    defaultMapper.writeValueAsString(NullableDto(Nullable("even"))),
                )
            }

            @Test
            fun `null`() {
                assertEquals(
                    """{"v":null}""",
                    defaultMapper.writeValueAsString(NullableDto(Nullable("odd"))),
                )
            }
        }
    }
}
