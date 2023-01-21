package com.fasterxml.jackson.module.kotlin._integration.ser.value_class.serializer.by_annotation.primitive

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.module.kotlin._integration.ser.value_class.serializer.Primitive
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.testPrettyWriter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class ByAnnotationTest {
    companion object {
        val writer = jacksonObjectMapper().testPrettyWriter()
    }

    data class NonNullSrc(
        @get:JsonSerialize(using = Primitive.Serializer::class)
        val getterAnn: Primitive,
        @field:JsonSerialize(using = Primitive.Serializer::class)
        val fieldAnn: Primitive
    )

    @Test
    fun nonNull() {
        val src = NonNullSrc(Primitive(0), Primitive(1))

        assertEquals(
            """
                {
                  "getterAnn" : 0,
                  "fieldAnn" : 1
                }
            """.trimIndent(),
            writer.writeValueAsString(src)
        )
    }

    data class NullableSrc(
        @get:JsonSerialize(using = Primitive.Serializer::class)
        val getterAnn: Primitive?,
        @field:JsonSerialize(using = Primitive.Serializer::class)
        val fieldAnn: Primitive?
    )

    @Test
    fun nullableWithoutNull() {
        val src = NullableSrc(Primitive(0), Primitive(1))

        assertEquals(
            """
                {
                  "getterAnn" : 0,
                  "fieldAnn" : 1
                }
            """.trimIndent(),
            writer.writeValueAsString(src)
        )
    }

    @Test
    fun nullableWithNull() {
        val src = NullableSrc(null, null)

        assertEquals(
            """
                {
                  "getterAnn" : null,
                  "fieldAnn" : null
                }
            """.trimIndent(),
            writer.writeValueAsString(src)
        )
    }

    data class Failing(
        @JsonSerialize(using = Primitive.Serializer::class)
        val nonNull: Primitive,
        @JsonSerialize(using = Primitive.Serializer::class)
        val nullable: Primitive?
    )

    // #46
    @Test
    fun failing() {
        val src = Failing(Primitive(0), Primitive(1))

        assertNotEquals(
            """
                {
                  "nonNull" : 0,
                  "nullable" : 1
                }
            """.trimIndent(),
            writer.writeValueAsString(src)
        )
    }
}
