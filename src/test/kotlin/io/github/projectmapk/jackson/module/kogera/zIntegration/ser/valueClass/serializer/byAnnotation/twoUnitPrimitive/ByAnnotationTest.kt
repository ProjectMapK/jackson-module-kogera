package io.github.projectmapk.jackson.module.kogera.zIntegration.ser.valueClass.serializer.byAnnotation.twoUnitPrimitive

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.github.projectmapk.jackson.module.kogera.KotlinFeature
import io.github.projectmapk.jackson.module.kogera.KotlinModule
import io.github.projectmapk.jackson.module.kogera.testPrettyWriter
import io.github.projectmapk.jackson.module.kogera.zIntegration.ser.valueClass.serializer.TwoUnitPrimitive
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
        @JsonSerialize(using = TwoUnitPrimitive.Serializer::class)
        val paramAnn: TwoUnitPrimitive,
        @get:JsonSerialize(using = TwoUnitPrimitive.Serializer::class)
        val getterAnn: TwoUnitPrimitive,
        @field:JsonSerialize(using = TwoUnitPrimitive.Serializer::class)
        val fieldAnn: TwoUnitPrimitive,
    )

    @Test
    fun nonNull() {
        val src = NonNullSrc(TwoUnitPrimitive(0), TwoUnitPrimitive(1), TwoUnitPrimitive(2))

        assertEquals(
            """
                {
                  "paramAnn" : 100,
                  "getterAnn" : 101,
                  "fieldAnn" : 102
                }
            """.trimIndent(),
            writer.writeValueAsString(src),
        )
    }

    data class NullableSrc(
        @JsonSerialize(using = TwoUnitPrimitive.Serializer::class)
        val paramAnn: TwoUnitPrimitive?,
        @get:JsonSerialize(using = TwoUnitPrimitive.Serializer::class)
        val getterAnn: TwoUnitPrimitive?,
        @field:JsonSerialize(using = TwoUnitPrimitive.Serializer::class)
        val fieldAnn: TwoUnitPrimitive?,
    )

    @Test
    fun nullableWithoutNull() {
        val src = NullableSrc(TwoUnitPrimitive(0), TwoUnitPrimitive(1), TwoUnitPrimitive(2))

        assertEquals(
            """
                {
                  "paramAnn" : 100,
                  "getterAnn" : 101,
                  "fieldAnn" : 102
                }
            """.trimIndent(),
            writer.writeValueAsString(src),
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
            writer.writeValueAsString(src),
        )
    }
}
