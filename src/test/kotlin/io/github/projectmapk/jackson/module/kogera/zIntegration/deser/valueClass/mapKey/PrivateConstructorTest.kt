package io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.mapKey

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
    inner class DirectDeserialize {
        @Test
        fun primitive() {
            val result = defaultMapper.readValue<Map<Primitive, String?>>("""{"1":null}""")
            assertEquals(1, result.keys.first().v)
        }

        @Test
        fun nonNullObject() {
            val result = defaultMapper.readValue<Map<NonNullObject, String?>>("""{"foo":null}""")
            assertEquals("foo", result.keys.first().v)
        }

        @Test
        fun nullableObject() {
            val result = defaultMapper.readValue<Map<NullableObject, String?>>("""{"bar":null}""")
            assertEquals("bar", result.keys.first().v)
        }

        @Test
        fun nullablePrimitive() {
            val result = defaultMapper.readValue<Map<NullablePrimitive, String?>>("""{"2":null}""")
            assertEquals(2, result.keys.first().v)
        }

        @Test
        fun twoUnitPrimitive() {
            val result = defaultMapper.readValue<Map<TwoUnitPrimitive, String?>>("""{"1":null}""")
            assertEquals(1L, result.keys.first().v)
        }
    }

    data class Dst(
        val p: Map<Primitive, String?>,
        val nn: Map<NonNullObject, String?>,
        val n: Map<NullableObject, String?>,
        val np: Map<NullablePrimitive, String?>,
        val tup: Map<TwoUnitPrimitive, String?>,
    )

    @Test
    fun wrapped() {
        val src = """
            {
              "p":{"1":null},
              "nn":{"foo":null},
              "n":{"bar":null},
              "np":{"2":null},
              "tup":{"2":null}
            }
        """.trimIndent()
        val result = defaultMapper.readValue<Dst>(src)
        assertEquals(1, result.p.keys.first().v)
        assertEquals("foo", result.nn.keys.first().v)
        assertEquals("bar", result.n.keys.first().v)
        assertEquals(2, result.np.keys.first().v)
        assertEquals(2L, result.tup.keys.first().v)
    }
}
