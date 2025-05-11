package io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.deserializer.byAnnotation.specifiedForProperty

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.TwoUnitPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TwoUnitPrimitiveTest {
    data class NonNull(
        @get:JsonDeserialize(using = TwoUnitPrimitive.Deserializer::class)
        val getterAnn: TwoUnitPrimitive,
        @field:JsonDeserialize(using = TwoUnitPrimitive.Deserializer::class)
        val fieldAnn: TwoUnitPrimitive
    )

    @Test
    fun nonNull() {
        val result = defaultMapper.readValue<NonNull>(
            """
                {
                  "getterAnn" : 1,
                  "fieldAnn" : 2
                }
            """.trimIndent()
        )
        assertEquals(NonNull(TwoUnitPrimitive(101), TwoUnitPrimitive(102)), result)
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
            val result = defaultMapper.readValue<Nullable>(
                """
                {
                  "getterAnn" : 1,
                  "fieldAnn" : 2
                }
                """.trimIndent()
            )
            assertEquals(Nullable(TwoUnitPrimitive(101), TwoUnitPrimitive(102)), result)
        }

        @Test
        fun nullInput() {
            val result = defaultMapper.readValue<Nullable>(
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
