package io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.deserializer.byAnnotation.specifiedForProperty

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.TwoUnitPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TwoUnitPrimitiveTest {
    companion object {
        val mapper = jacksonObjectMapper()
    }

    data class NonNull(
        @get:JsonDeserialize(using = TwoUnitPrimitive.Deserializer::class)
        val getterAnn: TwoUnitPrimitive,
        @field:JsonDeserialize(using = TwoUnitPrimitive.Deserializer::class)
        val fieldAnn: TwoUnitPrimitive
    )

    @Test
    fun nonNull() {
        val result = mapper.readValue<NonNull>(
            """
                {
                  "getterAnn" : 1,
                  "fieldAnn" : 2
                }
            """.trimIndent()
        )
        assertEquals(NonNull(TwoUnitPrimitive(101.0), TwoUnitPrimitive(102.0)), result)
    }

    data class Nullable(
        @get:JsonDeserialize(using = TwoUnitPrimitive.Deserializer::class)
        val getterAnn: TwoUnitPrimitive?,
        @field:JsonDeserialize(using = TwoUnitPrimitive.Deserializer::class)
        val fieldAnn: TwoUnitPrimitive?
    )

    @Nested
    inner class NullableTest {
        @Test
        fun nonNullInput() {
            val result = mapper.readValue<Nullable>(
                """
                {
                  "getterAnn" : 1,
                  "fieldAnn" : 2
                }
                """.trimIndent()
            )
            assertEquals(Nullable(TwoUnitPrimitive(101.0), TwoUnitPrimitive(102.0)), result)
        }

        @Test
        fun nullInput() {
            val result = mapper.readValue<Nullable>(
                """
                {
                  "getterAnn" : null,
                  "fieldAnn" : null
                }
                """.trimIndent()
            )
            assertEquals(Nullable(null, null), result)
        }
    }
}
