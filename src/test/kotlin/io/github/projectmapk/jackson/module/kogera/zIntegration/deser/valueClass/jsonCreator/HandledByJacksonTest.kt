package io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.jsonCreator

import com.fasterxml.jackson.annotation.JsonCreator
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// Test for Creator that can be handled by the Jackson mechanism.
class HandledByJacksonTest {
    @JvmInline
    value class PrimitiveMultiParamCreator(val value: Int) {
        companion object {
            // Avoiding unboxing by making the return value of Creator nullable
            @JvmStatic
            @JsonCreator
            fun creator(first: Int, second: Int): PrimitiveMultiParamCreator? = PrimitiveMultiParamCreator(
                first + second
            )
        }
    }

    @Test
    fun primitiveNullableCreatorTest() {
        val mapper = jacksonObjectMapper()
        val r: PrimitiveMultiParamCreator = mapper.readValue("""{"first":1,"second":2}""")
        assertEquals(PrimitiveMultiParamCreator(3), r)
    }

    @JvmInline
    value class NullableObjectMultiParamCreator(val value: Int?) {
        companion object {
            // Avoiding unboxing by making the return value of Creator nullable
            @JvmStatic
            @JsonCreator
            fun creator(first: Int, second: Int): NullableObjectMultiParamCreator? = NullableObjectMultiParamCreator(
                first + second
            )
        }
    }

    @Test
    fun nullableObjectNullableCreatorTest() {
        val mapper = jacksonObjectMapper()
        val r: NullableObjectMultiParamCreator = mapper.readValue("""{"first":1,"second":2}""")
        assertEquals(NullableObjectMultiParamCreator(3), r)
    }
}
