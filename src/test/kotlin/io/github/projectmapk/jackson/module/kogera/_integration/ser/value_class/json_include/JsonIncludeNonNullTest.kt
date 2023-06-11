package io.github.projectmapk.jackson.module.kogera._integration.ser.value_class.json_include

import com.fasterxml.jackson.annotation.JsonInclude
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class JsonIncludeNonNullTest {
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    data class Dto(
        val pN: Primitive? = null,
        val nnoN: NonNullObject? = null,
        val noN1: NullableObject? = null
    )

    @Test
    fun success() {
        val mapper = jacksonObjectMapper()
        val dto = Dto()
        assertEquals("{}", mapper.writeValueAsString(dto))
    }

    // It is under consideration whether it should be serialized because it is non-null in Kotlin,
    // but it is tentatively regarded as a failure.
    @JsonInclude(value = JsonInclude.Include.NON_NULL, content = JsonInclude.Include.NON_NULL)
    data class DtoFails(
        val noNn: NullableObject = NullableObject(null),
        val noN2: NullableObject? = NullableObject(null),
        val map: Map<Any, Any?> = mapOf("noNn" to NullableObject(null))
    )

    @Test
    fun fails() {
        val mapper = jacksonObjectMapper()
        val dto = DtoFails()
        val result = mapper.writeValueAsString(dto)
        assertNotEquals("""{"map":{}}""", result)
        assertEquals("""{"noNn":null,"noN2":null,"map":{"noNn":null}}""", result)
    }
}
