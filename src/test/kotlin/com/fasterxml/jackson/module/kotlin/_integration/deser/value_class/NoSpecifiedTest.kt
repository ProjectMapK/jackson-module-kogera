package com.fasterxml.jackson.module.kotlin._integration.deser.value_class

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
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

    // region Kogera42
    // After https://github.com/ProjectMapK/jackson-module-kogera/issues/42 is resolved, modify the test.
    data class WithoutNoNn(
        val pNn: Primitive,
        val pN: Primitive?,
        val nnoNn: NonNullObject,
        val nnoN: NonNullObject?,
        // val noNn: NullableObject,
        val noN: NullableObject?
    )

    @Test
    fun withNull() {
        val expected = WithoutNoNn(
            Primitive(1),
            null,
            NonNullObject("foo"),
            null,
            // NullableObject(null),
            null
        )
        val src = mapper.writeValueAsString(expected)
        val result = mapper.readValue<WithoutNoNn>(src)

        assertEquals(expected, result)
    }

    data class Failing(val noNn: NullableObject)

    @Test
    fun failing() {
        val expected = Failing(NullableObject(null))
        val src = mapper.writeValueAsString(expected)

        assertThrows<MissingKotlinParameterException>("Kogera #42 is fixed") {
            val result = mapper.readValue<Failing>(src)
            assertEquals(expected, result)
        }
    }
    // endregion
}
