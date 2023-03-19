package com.fasterxml.jackson.module.kotlin._integration.ser.value_class.json_include

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.support.ValueClassSupport.boxedValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class JsonIncludeCustomTest {
    class NullFilter {
        override fun equals(other: Any?) = other == null
    }

    /**
     * This Filter is used to filter out null value of a **`value class`**.
     * same as JSON_INCLUDE_NON_NULL in jackson-databind
     */
    private class KotlinValueClassNullFilter {
        override fun equals(other: Any?): Boolean = other == null || other.boxedValue == null
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

    @JsonInclude(
        value = JsonInclude.Include.CUSTOM,
        valueFilter = KotlinValueClassNullFilter::class
    )
    data class KotlinValueClassNullFilterDto(
            val pN: Primitive? = null,
            val nnoN: NonNullObject? = null,
            val noN1: NullableObject? = NullableObject(null)
    )

    @Test
    fun nullFilterTest() {
        val mapper = jacksonObjectMapper()
        val dto = NullFilterDto()
        assertEquals("{}", mapper.writeValueAsString(dto))
    }

    @Test
    fun kotlinValueClassNullFilterTest() {
        val mapper = jacksonObjectMapper()
        val dto = KotlinValueClassNullFilterDto()
        assertEquals("{}", mapper.writeValueAsString(dto))
    }
}
