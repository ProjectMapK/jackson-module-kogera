package io.github.projectmapk.jackson.module.kogera.zIntegration.ser.valueClass.jsonValue

import com.fasterxml.jackson.annotation.JsonValue
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class NullablePrimitiveTest {
    @JvmInline
    value class Value(val v: Int?) {
        @JsonValue
        fun jsonValue() = v?.let { it + 100 }
    }

    @Nested
    inner class DirectTest {
        @Test
        fun nonNull() {
            assertEquals(
                "100",
                defaultMapper.writeValueAsString(Value(0)),
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
                """{"v":100}""",
                defaultMapper.writeValueAsString(Dto(Value(0))),
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
