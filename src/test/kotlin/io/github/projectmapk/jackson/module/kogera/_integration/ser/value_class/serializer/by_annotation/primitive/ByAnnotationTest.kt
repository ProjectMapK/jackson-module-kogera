package io.github.projectmapk.jackson.module.kogera._integration.ser.value_class.serializer.by_annotation.primitive

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.github.projectmapk.jackson.module.kogera._integration.ser.value_class.serializer.Primitive
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.testPrettyWriter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ByAnnotationTest {
    companion object {
        val writer = jacksonObjectMapper().testPrettyWriter()
    }

    data class NonNullSrc(
        @JsonSerialize(using = Primitive.Serializer::class)
        val paramAnn: Primitive,
        @get:JsonSerialize(using = Primitive.Serializer::class)
        val getterAnn: Primitive,
        @field:JsonSerialize(using = Primitive.Serializer::class)
        val fieldAnn: Primitive
    )

    @Test
    fun nonNull() {
        val src = NonNullSrc(Primitive(0), Primitive(1), Primitive(2))

        assertEquals(
            """
                {
                  "paramAnn" : 100,
                  "getterAnn" : 101,
                  "fieldAnn" : 102
                }
            """.trimIndent(),
            writer.writeValueAsString(src)
        )
    }

    data class NullableSrc(
        @JsonSerialize(using = Primitive.Serializer::class)
        val paramAnn: Primitive?,
        @get:JsonSerialize(using = Primitive.Serializer::class)
        val getterAnn: Primitive?,
        @field:JsonSerialize(using = Primitive.Serializer::class)
        val fieldAnn: Primitive?
    )

    @Test
    fun nullableWithoutNull() {
        val src = NullableSrc(Primitive(0), Primitive(1), Primitive(2))

        assertEquals(
            """
                {
                  "paramAnn" : 100,
                  "getterAnn" : 101,
                  "fieldAnn" : 102
                }
            """.trimIndent(),
            writer.writeValueAsString(src)
        )
    }

    @Test
    fun nullableWithNull() {
        val src = NullableSrc(null, null, null)

        assertEquals(
            """
                {
                  "paramAnn" : null,
                  "getterAnn" : null,
                  "fieldAnn" : null
                }
            """.trimIndent(),
            writer.writeValueAsString(src)
        )
    }
}
