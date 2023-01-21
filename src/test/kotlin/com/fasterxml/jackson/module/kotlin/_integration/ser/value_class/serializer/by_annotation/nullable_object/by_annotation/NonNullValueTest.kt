package com.fasterxml.jackson.module.kotlin._integration.ser.value_class.serializer.by_annotation.nullable_object.by_annotation

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.module.kotlin._integration.ser.value_class.serializer.NullableObject
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.testPrettyWriter
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class NonNullValueTest {
    companion object {
        val writer = jacksonObjectMapper().testPrettyWriter()
    }

    data class NonNullSrc(
        @get:JsonSerialize(using = NullableObject.Serializer::class)
        val getterAnn: NullableObject,
        @field:JsonSerialize(using = NullableObject.Serializer::class)
        val fieldAnn: NullableObject
    )

    @Test
    fun nonNull() {
        val src = NonNullSrc(NullableObject("foo"), NullableObject("bar"))

        Assertions.assertEquals(
            """
                {
                  "getterAnn" : "foo-ser",
                  "fieldAnn" : "bar-ser"
                }
            """.trimIndent(),
            writer.writeValueAsString(src)
        )
    }

    data class NullableSrc(
        @get:JsonSerialize(using = NullableObject.Serializer::class)
        val getterAnn: NullableObject?,
        @field:JsonSerialize(using = NullableObject.Serializer::class)
        val fieldAnn: NullableObject?
    )

    @Test
    fun nullableWithoutNull() {
        val src = NullableSrc(NullableObject("foo"), NullableObject("bar"))

        Assertions.assertEquals(
            """
                {
                  "getterAnn" : "foo-ser",
                  "fieldAnn" : "bar-ser"
                }
            """.trimIndent(),
            writer.writeValueAsString(src)
        )
    }

    @Test
    fun nullableWithNull() {
        val src = NullableSrc(null, null)

        Assertions.assertEquals(
            """
                {
                  "getterAnn" : null,
                  "fieldAnn" : null
                }
            """.trimIndent(),
            writer.writeValueAsString(src)
        )
    }
}
