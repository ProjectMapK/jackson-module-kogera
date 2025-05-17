package io.github.projectmapk.jackson.module.kogera.zIntegration.ser.valueClass.jsonInclude

import com.fasterxml.jackson.annotation.JsonInclude
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class JsonIncludeNonNullTest {
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    data class Dto(
        val pN: Primitive? = null,
        val nnoN: NonNullObject? = null,
        val noN1: NullableObject? = null,
        val npN: NullablePrimitive? = null,
        val tupN: TwoUnitPrimitive? = null
    )

    @Test
    fun success() {
        val dto = Dto()
        assertEquals("{}", defaultMapper.writeValueAsString(dto))
    }

    // It is under consideration whether it should be serialized because it is non-null in Kotlin,
    // but it is tentatively regarded as a failure.
    @JsonInclude(value = JsonInclude.Include.NON_NULL, content = JsonInclude.Include.NON_NULL)
    data class DtoFails(
        val noNn: NullableObject = NullableObject(null),
        val noN2: NullableObject? = NullableObject(null),
        val npNn: NullablePrimitive = NullablePrimitive(null),
        val npN2: NullablePrimitive? = NullablePrimitive(null),
        val map: Map<Any, Any?> = mapOf(
            "noNn" to NullableObject(null),
            "npNn" to NullablePrimitive(null)
        )
    )

    @Test
    fun fails() {
        val dto = DtoFails()
        val result = defaultMapper.writeValueAsString(dto)
        assertNotEquals("""{"map":{}}""", result)
        assertEquals("""{"noNn":null,"noN2":null,"npNn":null,"npN2":null,"map":{"noNn":null,"npNn":null}}""", result)
    }
}
