package io.github.projectmapk.jackson.module.kogera.zIntegration.ser.valueClass.jsonKey

import com.fasterxml.jackson.annotation.JsonKey
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NonNullObjectTest {
    @JvmInline
    value class NonNull(val v: String) {
        @JsonKey
        fun jsonValue() = v + "_modified"
    }

    @Test
    fun nonNullTest() {
        assertEquals(
            """{"test_modified":null}""",
            defaultMapper.writeValueAsString(mapOf(NonNull("test") to null)),
        )
    }

    @JvmInline
    value class Nullable(val v: String) {
        @JsonKey
        fun jsonValue() = v.takeIf { it.length % 2 == 0 }?.let { it + "_modified" }
    }

    // The case of returning null as a key is unnecessary because it will result in an error
    @Test
    fun nullableTest() {
        assertEquals(
            """{"test_modified":null}""",
            defaultMapper.writeValueAsString(mapOf(Nullable("test") to null)),
        )
    }
}
