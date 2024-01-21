package io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass

import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.reflect.InvocationTargetException

class WithoutCustomDeserializeMethodTest {
    companion object {
        val mapper = jacksonObjectMapper()
        val throwable = IllegalArgumentException("test")
    }

    @Nested
    inner class DirectDeserializeTest {
        @Test
        fun primitive() {
            val result = defaultMapper.readValue<Primitive>("1")
            assertEquals(Primitive(1), result)
        }

        @Test
        fun nonNullObject() {
            val result = defaultMapper.readValue<NonNullObject>(""""foo"""")
            assertEquals(NonNullObject("foo"), result)
        }

        @Suppress("ClassName")
        @Nested
        inner class NullableObject_ {
            @Test
            fun value() {
                val result = defaultMapper.readValue<NullableObject>(""""foo"""")
                assertEquals(NullableObject("foo"), result)
            }

            // failing
            @Test
            fun nullString() {
                assertThrows<NullPointerException>("#209 has been fixed.") {
                    val result = defaultMapper.readValue<NullableObject>("null")
                    assertEquals(NullableObject(null), result)
                }
            }
        }
    }

    data class Dst(
        val pNn: Primitive,
        val pN: Primitive?,
        val nnoNn: NonNullObject,
        val nnoN: NonNullObject?,
        val noNn: NullableObject,
        val noN: NullableObject?
    )

    @Test
    fun withoutNull() {
        val expected = Dst(
            Primitive(1),
            Primitive(2),
            NonNullObject("foo"),
            NonNullObject("bar"),
            NullableObject("baz"),
            NullableObject("qux")
        )
        val src = mapper.writeValueAsString(expected)
        val result = mapper.readValue<Dst>(src)

        assertEquals(expected, result)
    }

    @Test
    fun withNull() {
        val expected = Dst(
            Primitive(1),
            null,
            NonNullObject("foo"),
            null,
            NullableObject(null),
            null
        )
        val src = mapper.writeValueAsString(expected)
        val result = mapper.readValue<Dst>(src)

        assertEquals(expected, result)
    }

    @JvmInline
    value class HasCheckConstructor(val value: Int) {
        init {
            if (value < 0) throw throwable
        }
    }

    @Test
    fun callConstructorCheckTest() {
        val e = assertThrows<InvocationTargetException> { defaultMapper.readValue<HasCheckConstructor>("-1") }
        Assertions.assertTrue(e.cause === throwable)
    }

    // If all JsonCreator tests are OK, no need to check throws from factory functions.
}
