package io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass

import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PrivateConstructorTest {
    @JvmInline
    value class Primitive private constructor(val v: Int)

    @JvmInline
    value class NonNullObject private constructor(val v: String)

    @JvmInline
    value class NullableObject private constructor(val v: String?)

    @JvmInline
    value class NullablePrimitive private constructor(val v: Int?)

    @JvmInline
    value class TwoUnitPrimitive private constructor(val v: Long)

    @Nested
    inner class DirectDeserializeTest {
        @Test
        fun primitiveTest() {
            val result = defaultMapper.readValue<Primitive>("1")
            assertEquals(1, result.v)
        }

        @Test
        fun nonNullObjectTest() {
            val result = defaultMapper.readValue<NonNullObject>(""""foo"""")
            assertEquals("foo", result.v)
        }

        @Test
        fun nullableObjectTest() {
            val result = defaultMapper.readValue<NullableObject>(""""bar"""")
            assertEquals("bar", result.v)
        }

        @Test
        fun nullablePrimitiveTest() {
            val result = defaultMapper.readValue<NullablePrimitive>("2")
            assertEquals(2, result.v)
        }

        @Test
        fun twoUnitPrimitiveTest() {
            val result = defaultMapper.readValue<TwoUnitPrimitive>("3")
            assertEquals(3L, result.v)
        }
    }

    data class Dto(
        val primitive: Primitive,
        val nonNullObject: NonNullObject,
        val nullableObject: NullableObject,
        val nullablePrimitive: NullablePrimitive,
        val twoUnitPrimitive: TwoUnitPrimitive,
    )

    @Test
    fun wrappedDeserializeTest() {
        val src = """{"primitive":1,"nonNullObject":"foo","nullableObject":"bar","nullablePrimitive":2,"twoUnitPrimitive":3}"""
        val result = defaultMapper.readValue<Dto>(src)
        assertEquals(1, result.primitive.v)
        assertEquals("foo", result.nonNullObject.v)
        assertEquals("bar", result.nullableObject.v)
        assertEquals(2, result.nullablePrimitive.v)
        assertEquals(3L, result.twoUnitPrimitive.v)
    }
}
