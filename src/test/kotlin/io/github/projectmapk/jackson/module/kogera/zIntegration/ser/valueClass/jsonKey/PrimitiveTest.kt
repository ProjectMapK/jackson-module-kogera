package io.github.projectmapk.jackson.module.kogera.zIntegration.ser.valueClass.jsonKey

import com.fasterxml.jackson.annotation.JsonKey
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PrimitiveTest {
    @JvmInline
    value class NonNull(val v: Int) {
        @JsonKey
        fun jsonValue() = v + 100
    }

    @Test
    fun nonNullTest() {
        assertEquals(
            """{"100":null}""",
            defaultMapper.writeValueAsString(mapOf(NonNull(0) to null)),
        )
    }

    @JvmInline
    value class Nullable(val v: Int) {
        @JsonKey
        fun jsonValue() = v.takeIf { it % 2 == 0 }?.let { it + 100 }
    }

    // The case of returning null as a key is unnecessary because it will result in an error
    @Test
    fun nullableTest() {
        assertEquals(
            """{"100":null}""",
            defaultMapper.writeValueAsString(mapOf(Nullable(0) to null)),
        )
    }
}
