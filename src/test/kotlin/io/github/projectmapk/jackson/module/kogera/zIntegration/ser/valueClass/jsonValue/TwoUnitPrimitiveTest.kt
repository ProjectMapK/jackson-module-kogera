package io.github.projectmapk.jackson.module.kogera.zIntegration.ser.valueClass.jsonValue

import com.fasterxml.jackson.annotation.JsonValue
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TwoUnitPrimitiveTest {
    @JvmInline
    value class NonNull(val v: Long) {
        @JsonValue
        fun jsonValue() = v + 100
    }

    data class NonNullDto(val v: NonNull)

    @Nested
    inner class NonNullTest {
        @Test
        fun direct() {
            assertEquals(
                "100",
                defaultMapper.writeValueAsString(NonNull(0)),
            )
        }

        @Test
        fun asProperty() {
            assertEquals(
                """{"v":100}""",
                defaultMapper.writeValueAsString(NonNullDto(NonNull(0))),
            )
        }
    }

    @JvmInline
    value class Nullable(val v: Long) {
        @JsonValue
        fun jsonValue() = v.takeIf { it % 2L == 0L }?.let { it + 100 }
    }

    data class NullableDto(val v: Nullable)

    @Nested
    inner class NullableTest {
        @Nested
        inner class DirectTest {
            @Test
            fun nonNull() {
                assertEquals(
                    "100",
                    defaultMapper.writeValueAsString(Nullable(0)),
                )
            }

            @Test
            fun `null`() {
                assertEquals(
                    "null",
                    defaultMapper.writeValueAsString(Nullable(1)),
                )
            }
        }

        @Nested
        inner class AsPropertyTest {
            @Test
            fun nonNull() {
                assertEquals(
                    """{"v":100}""",
                    defaultMapper.writeValueAsString(NullableDto(Nullable(0))),
                )
            }

            @Test
            fun `null`() {
                assertEquals(
                    """{"v":null}""",
                    defaultMapper.writeValueAsString(NullableDto(Nullable(1))),
                )
            }
        }
    }
}
