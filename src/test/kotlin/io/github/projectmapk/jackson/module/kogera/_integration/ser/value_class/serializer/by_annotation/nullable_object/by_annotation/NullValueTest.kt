package io.github.projectmapk.jackson.module.kogera._integration.ser.value_class.serializer.by_annotation.nullable_object.by_annotation

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.github.projectmapk.jackson.module.kogera._integration.ser.value_class.serializer.NullableObject
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.testPrettyWriter
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class NullValueTest {
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
    fun failing() {
        val src = NonNullSrc(NullableObject(null), NullableObject(null))

        Assertions.assertNotEquals(
            """
                {
                  "getterAnn" : "NULL",
                  "fieldAnn" : "NULL"
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
        val src = NullableSrc(NullableObject(null), NullableObject(null))

        Assertions.assertEquals(
            """
                {
                  "getterAnn" : "NULL",
                  "fieldAnn" : "NULL"
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
