package io.github.projectmapk.jackson.module.kogera.zIntegration.ser.valueClass

import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.testPrettyWriter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class WithoutCustomSerializeMethodTest {
    @JvmInline
    value class Primitive(val v: Int)

    @JvmInline
    value class NonNullObject(val v: String)

    @JvmInline
    value class NullableObject(val v: String?)

    @JvmInline
    value class NullablePrimitive(val v: Int?)

    @JvmInline
    value class TwoUnitPrimitive(val v: Long)

    private val writer = defaultMapper.testPrettyWriter()

    @Nested
    inner class DirectSerializeTest {
        @Test
        fun primitive() {
            val result = writer.writeValueAsString(Primitive(1))
            assertEquals("1", result)
        }

        @Test
        fun nonNullObject() {
            val result = writer.writeValueAsString(NonNullObject("foo"))
            assertEquals("\"foo\"", result)
        }

        @Suppress("ClassName")
        @Nested
        inner class NullableObject_ {
            @Test
            fun value() {
                val result = writer.writeValueAsString(NullableObject("foo"))
                assertEquals("\"foo\"", result)
            }

            @Test
            fun nullValue() {
                val result = writer.writeValueAsString(NullableObject(null))
                assertEquals("null", result)
            }
        }

        @Suppress("ClassName")
        @Nested
        inner class NullablePrimitive_ {
            @Test
            fun value() {
                val result = writer.writeValueAsString(NullablePrimitive(1))
                assertEquals("1", result)
            }

            @Test
            fun nullValue() {
                val result = writer.writeValueAsString(NullablePrimitive(null))
                assertEquals("null", result)
            }
        }

        @Test
        fun twoUnitPrimitive() {
            val result = writer.writeValueAsString(TwoUnitPrimitive(1))
            assertEquals("1", result)
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
    fun withoutNull() {
        val src = Src(
            Primitive(1),
            Primitive(2),
            NonNullObject("foo"),
            NonNullObject("bar"),
            NullableObject("baz"),
            NullableObject("qux"),
            NullablePrimitive(1),
            NullablePrimitive(2),
            TwoUnitPrimitive(3),
            TwoUnitPrimitive(4),
        )
        val result = writer.writeValueAsString(src)

        assertEquals(
            """
            {
              "pNn" : 1,
              "pN" : 2,
              "nnoNn" : "foo",
              "nnoN" : "bar",
              "noNn" : "baz",
              "noN" : "qux",
              "npNn" : 1,
              "npN" : 2,
              "tupNn" : 3,
              "tupN" : 4
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
            TwoUnitPrimitive(3),
            null,
        )
        val result = writer.writeValueAsString(src)

        assertEquals(
            """
            {
              "pNn" : 1,
              "pN" : null,
              "nnoNn" : "foo",
              "nnoN" : null,
              "noNn" : null,
              "noN" : null,
              "npNn" : null,
              "npN" : null,
              "tupNn" : 3,
              "tupN" : null
            }
            """.trimIndent(),
            result,
        )
    }

    @JvmInline
    value class HasToString(val value: Int) {
        override fun toString(): String = "Custom($value)"
    }

    @Test
    fun toStringTest() {
        val result = writer.writeValueAsString(HasToString(42))
        assertEquals("42", result)
    }
}
