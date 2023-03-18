package com.fasterxml.jackson.module.kotlin._integration.ser.value_class.json_include

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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
        val noN1: NullableObject? = null
    )

    @Test
    fun nullFilterTest() {
        val mapper = jacksonObjectMapper()
        val dto = NullFilterDto()
        assertEquals("{}", mapper.writeValueAsString(dto))
    }
}
