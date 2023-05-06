package io.github.projectmapk.jackson.module.kogera._integration.deser.value_class.json_creator

import com.fasterxml.jackson.annotation.JsonCreator
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// Test for Creator that can be handled by the Jackson mechanism.
class ValueClassByJacksonCreatorTest {
    @JvmInline
    value class PrimitiveNullableCreator(val value: Int) {
        companion object {
            // Avoiding unboxing by making the return value of Creator nullable
            @JvmStatic
            @JsonCreator
            fun creator(first: Int, second: Int): PrimitiveNullableCreator? =
                PrimitiveNullableCreator(first + second)
        }
    }

    @Test
    fun primitiveNullableCreatorTest() {
        val mapper = jacksonObjectMapper()
        val r: PrimitiveNullableCreator = mapper.readValue("""{"first":1,"second":2}""")
        assertEquals(PrimitiveNullableCreator(3), r)
    }

    @JvmInline
    value class NullableObjectNullableCreator(val value: Int?) {
        companion object {
            // Avoiding unboxing by making the return value of Creator nullable
            @JvmStatic
            @JsonCreator
            fun creator(first: Int, second: Int): NullableObjectNullableCreator? =
                NullableObjectNullableCreator(first + second)
        }
    }

    @Test
    fun nullableObjectNullableCreatorTest() {
        val mapper = jacksonObjectMapper()
        val r: NullableObjectNullableCreator = mapper.readValue("""{"first":1,"second":2}""")
        assertEquals(NullableObjectNullableCreator(3), r)
    }
}
