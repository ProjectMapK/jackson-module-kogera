package io.github.projectmapk.jackson.module.kogera.zIntegration.ser.valueClass.jsonInclude

import com.fasterxml.jackson.annotation.JsonInclude
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class JsonIncludeCustomTest {
    class NullFilter {
        override fun equals(other: Any?) = other == null
    }

    @JsonInclude(
        value = JsonInclude.Include.CUSTOM,
        valueFilter = NullFilter::class
    )
    data class NullFilterDto(
        val pN: Primitive? = null,
        val nnoN: NonNullObject? = null,
        val noN1: NullableObject? = null,
        val tupN: TwoUnitPrimitive? = null
    )

    @Test
    fun nullFilterTest() {
        val mapper = jacksonObjectMapper()
        val dto = NullFilterDto()
        assertEquals("{}", mapper.writeValueAsString(dto))
    }
}
