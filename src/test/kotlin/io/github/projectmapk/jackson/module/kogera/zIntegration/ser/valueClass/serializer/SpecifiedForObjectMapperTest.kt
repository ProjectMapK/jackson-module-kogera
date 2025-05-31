package io.github.projectmapk.jackson.module.kogera.zIntegration.ser.valueClass.serializer

import com.fasterxml.jackson.databind.module.SimpleModule
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.testPrettyWriter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SpecifiedForObjectMapperTest {
    companion object {
        val mapper = jacksonObjectMapper().apply {
            val module = SimpleModule().apply {
                this.addSerializer(Primitive::class.java, Primitive.Serializer())
                this.addSerializer(NonNullObject::class.java, NonNullObject.Serializer())
                this.addSerializer(NullableObject::class.java, NullableObject.Serializer())
                this.addSerializer(NullablePrimitive::class.java, NullablePrimitive.Serializer())
                this.addSerializer(TwoUnitPrimitive::class.java, TwoUnitPrimitive.Serializer())
            }
            this.registerModule(module)
        }
        val writer = mapper.testPrettyWriter()
    }

    @Nested
    inner class DirectSerialize {
        @Test
        fun primitive() {
            val result = writer.writeValueAsString(Primitive(1))
            assertEquals("101", result)
        }

        @Test
        fun nonNullObject() {
            val result = writer.writeValueAsString(NonNullObject("foo"))
            assertEquals("\"foo-ser\"", result)
        }

        @Suppress("ClassName")
        @Nested
        inner class NullableObject_ {
            @Test
            fun value() {
                val result = writer.writeValueAsString(NullableObject("foo"))
                assertEquals("\"foo-ser\"", result)
            }

            @Test
            fun nullValue() {
                val result = writer.writeValueAsString(NullableObject(null))
                assertEquals("\"NULL\"", result)
            }
        }

        @Suppress("ClassName")
        @Nested
        inner class NullablePrimitive_ {
            @Test
            fun value() {
                val result = writer.writeValueAsString(NullablePrimitive(1))
                assertEquals("101", result)
            }

            @Test
            fun nullValue() {
                val result = writer.writeValueAsString(NullablePrimitive(null))
                assertEquals("\"NULL\"", result)
            }
        }

        @Test
        fun twoUnitPrimitive() {
            val result = writer.writeValueAsString(TwoUnitPrimitive(1))
            assertEquals("101", result)
        }
    }

    data class Src(
        val pNn: Primitive,
        val pN: Primitive?,
        val nnoNn: NonNullObject,
        val nnoN: NonNullObject?,
        val noNn: NullableObject,
        val noN: NullableObject?,
        val npNn: NullablePrimitive,
        val npN: NullablePrimitive?,
        val tupNn: TwoUnitPrimitive,
        val tupN: TwoUnitPrimitive?,
    )

    @Test
    fun nonNull() {
        val src = Src(
            Primitive(1),
            Primitive(2),
            NonNullObject("foo"),
            NonNullObject("bar"),
            NullableObject("baz"),
            NullableObject("qux"),
            NullablePrimitive(3),
            NullablePrimitive(4),
            TwoUnitPrimitive(5),
            TwoUnitPrimitive(6),
        )
        val result = writer.writeValueAsString(src)

        assertEquals(
            """
                {
                  "pNn" : 101,
                  "pN" : 102,
                  "nnoNn" : "foo-ser",
                  "nnoN" : "bar-ser",
                  "noNn" : "baz-ser",
                  "noN" : "qux-ser",
                  "npNn" : 103,
                  "npN" : 104,
                  "tupNn" : 105,
                  "tupN" : 106
                }
            """.trimIndent(),
            result,
        )
    }

    @Test
    fun withNull() {
        val src = Src(
            Primitive(1),
            null,
            NonNullObject("foo"),
            null,
            NullableObject(null),
            null,
            NullablePrimitive(null),
            null,
            TwoUnitPrimitive(5),
            null,
        )
        val result = writer.writeValueAsString(src)

        assertEquals(
            """
                {
                  "pNn" : 101,
                  "pN" : null,
                  "nnoNn" : "foo-ser",
                  "nnoN" : null,
                  "noNn" : "NULL",
                  "noN" : null,
                  "npNn" : "NULL",
                  "npN" : null,
                  "tupNn" : 105,
                  "tupN" : null
                }
            """.trimIndent(),
            result,
        )
    }
}
