package io.github.projectmapk.jackson.module.kogera.zIntegration.ser.valueClass.serializer.byAnnotation.primitive

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.github.projectmapk.jackson.module.kogera.KotlinFeature
import io.github.projectmapk.jackson.module.kogera.KotlinModule
import io.github.projectmapk.jackson.module.kogera.testPrettyWriter
import io.github.projectmapk.jackson.module.kogera.zIntegration.ser.valueClass.serializer.Primitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ByAnnotationTest {
    companion object {
        val writer = KotlinModule.Builder()
            .enable(KotlinFeature.CopySyntheticConstructorParameterAnnotations)
            .build()
            .let { ObjectMapper().registerModule(it) }
            .testPrettyWriter()
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
