package io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.deserializer.byAnnotation.specifiedForProperty

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.NonNullObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class NonNullObjectTest {
    data class NonNull(
        @get:JsonDeserialize(using = NonNullObject.Deserializer::class)
        val getterAnn: NonNullObject,
        @field:JsonDeserialize(using = NonNullObject.Deserializer::class)
        val fieldAnn: NonNullObject,
    )

    @Test
    fun nonNull() {
        val result = defaultMapper.readValue<NonNull>(
            """
                {
                  "getterAnn" : "foo",
                  "fieldAnn" : "bar"
                }
            """.trimIndent(),
        )
        assertEquals(NonNull(NonNullObject("foo-deser"), NonNullObject("bar-deser")), result)
    }

    data class Nullable(
        @get:JsonDeserialize(using = NonNullObject.Deserializer::class)
        val getterAnn: NonNullObject?,
        @field:JsonDeserialize(using = NonNullObject.Deserializer::class)
        val fieldAnn: NonNullObject?,
    )

    @Nested
    inner class NullableTest {
        @Test
        fun nonNullInput() {
            val result = defaultMapper.readValue<Nullable>(
                """
                {
                  "getterAnn" : "foo",
                  "fieldAnn" : "bar"
                }
                """.trimIndent(),
            )
            assertEquals(Nullable(NonNullObject("foo-deser"), NonNullObject("bar-deser")), result)
        }

        @Test
        fun nullInput() {
            val result = defaultMapper.readValue<Nullable>(
                """
                {
                  "getterAnn" : null,
                  "fieldAnn" : null
                }
                """.trimIndent(),
            )
            assertEquals(Nullable(null, null), result)
        }
    }
}
