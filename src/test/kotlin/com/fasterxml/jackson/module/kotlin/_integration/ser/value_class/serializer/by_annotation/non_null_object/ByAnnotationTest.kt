package com.fasterxml.jackson.module.kotlin._integration.ser.value_class.serializer.by_annotation.non_null_object

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.module.kotlin._integration.ser.value_class.serializer.NonNullObject
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.testPrettyWriter
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ByAnnotationTest {
    companion object {
        val writer = jacksonObjectMapper().testPrettyWriter()
    }

    data class NonNullSrc(
        @get:JsonSerialize(using = NonNullObject.Serializer::class)
        val getterAnn: NonNullObject,
        @field:JsonSerialize(using = NonNullObject.Serializer::class)
        val fieldAnn: NonNullObject
    )

    @Test
    fun nonNull() {
        val src = NonNullSrc(NonNullObject("foo"), NonNullObject("bar"))

        Assertions.assertEquals(
            """
                {
                  "getterAnn" : "foo",
                  "fieldAnn" : "bar"
                }
            """.trimIndent(),
            writer.writeValueAsString(src)
        )
    }

    data class NullableSrc(
        @get:JsonSerialize(using = NonNullObject.Serializer::class)
        val getterAnn: NonNullObject?,
        @field:JsonSerialize(using = NonNullObject.Serializer::class)
        val fieldAnn: NonNullObject?
    )

    @Test
    fun nullableWithoutNull() {
        val src = NullableSrc(NonNullObject("foo"), NonNullObject("bar"))

        Assertions.assertEquals(
            """
                {
                  "getterAnn" : "foo",
                  "fieldAnn" : "bar"
                }
            """.trimIndent(),
            writer.writeValueAsString(src)
        )
    }

    @Test
    fun failing() {
        val src = NullableSrc(null, null)

        Assertions.assertNotEquals(
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
