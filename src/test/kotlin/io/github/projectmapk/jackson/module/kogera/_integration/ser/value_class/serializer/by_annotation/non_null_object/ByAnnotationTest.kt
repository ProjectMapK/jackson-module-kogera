package io.github.projectmapk.jackson.module.kogera._integration.ser.value_class.serializer.by_annotation.non_null_object

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.github.projectmapk.jackson.module.kogera._integration.ser.value_class.serializer.NonNullObject
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.testPrettyWriter
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
                  "getterAnn" : "foo-ser",
                  "fieldAnn" : "bar-ser"
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
                  "getterAnn" : "foo-ser",
                  "fieldAnn" : "bar-ser"
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
