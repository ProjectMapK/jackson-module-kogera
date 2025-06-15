package io.github.projectmapk.jackson.module.kogera.zIntegration.ser.valueClass.jsonKey

import com.fasterxml.jackson.annotation.JsonKey
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NullableObjectTest {
    @JvmInline
    value class Value(val v: String?) {
        @JsonKey
        fun jsonValue() = v?.let { it + "_modified" }
    }

    // The case of returning null as a key is unnecessary because it will result in an error
    @Test
    fun test() {
        assertEquals(
            """{"test_modified":null}""",
            defaultMapper.writeValueAsString(mapOf(Value("test") to null)),
        )
    }
}
