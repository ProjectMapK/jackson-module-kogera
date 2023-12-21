package io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass

import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.reflect.InvocationTargetException

class NoSpecifiedTest {
    companion object {
        val mapper = jacksonObjectMapper()
        val throwable = IllegalArgumentException("test")
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
    fun nonNull() {
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
    value class HasCheck(val value: Int) {
        init {
            if (value < 0) throw throwable
        }
    }

    @Test
    fun callCheckTest() {
        val e = assertThrows<InvocationTargetException> { mapper.readValue<HasCheck>("-1") }
        assertTrue(e.cause === throwable)
    }
}
