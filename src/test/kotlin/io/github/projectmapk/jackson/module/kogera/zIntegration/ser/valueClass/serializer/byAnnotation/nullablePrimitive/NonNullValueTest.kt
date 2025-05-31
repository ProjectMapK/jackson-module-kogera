package io.github.projectmapk.jackson.module.kogera.zIntegration.ser.valueClass.serializer.byAnnotation.nullablePrimitive

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.testPrettyWriter
import io.github.projectmapk.jackson.module.kogera.zIntegration.ser.valueClass.serializer.NullablePrimitive
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class NonNullValueTest {
    companion object {
        val writer = jacksonObjectMapper().testPrettyWriter()
    }

    data class NonNullSrc(
        @get:JsonSerialize(using = NullablePrimitive.Serializer::class)
        val getterAnn: NullablePrimitive,
        @field:JsonSerialize(using = NullablePrimitive.Serializer::class)
        val fieldAnn: NullablePrimitive,
    )

    @Test
    fun nonNull() {
        val src = NonNullSrc(NullablePrimitive(42), NullablePrimitive(99))

        Assertions.assertEquals(
            """
                {
                  "getterAnn" : 142,
                  "fieldAnn" : 199
                }
            """.trimIndent(),
            writer.writeValueAsString(src),
        )
    }

    data class NullableSrc(
        @get:JsonSerialize(using = NullablePrimitive.Serializer::class)
        val getterAnn: NullablePrimitive?,
        @field:JsonSerialize(using = NullablePrimitive.Serializer::class)
        val fieldAnn: NullablePrimitive?,
    )

    @Test
    fun nullableWithoutNull() {
        val src = NullableSrc(NullablePrimitive(42), NullablePrimitive(99))

        Assertions.assertEquals(
            """
                {
                  "getterAnn" : 142,
                  "fieldAnn" : 199
                }
            """.trimIndent(),
            writer.writeValueAsString(src),
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
            writer.writeValueAsString(src),
        )
    }
}
