package io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.mapKey

import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException
import com.fasterxml.jackson.databind.module.SimpleModule
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.NonNullObject
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.NullableObject
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.Primitive
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.TwoUnitPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.reflect.InvocationTargetException
import com.fasterxml.jackson.databind.KeyDeserializer as JacksonKeyDeserializer

class WithoutCustomDeserializeMethodTest {
    companion object {
        val throwable = IllegalArgumentException("test")
    }

    @Nested
    inner class DirectDeserialize {
        @Test
        fun primitive() {
            val result = defaultMapper.readValue<Map<Primitive, String?>>("""{"1":null}""")
            assertEquals(mapOf(Primitive(1) to null), result)
        }

        @Test
        fun nonNullObject() {
            val result = defaultMapper.readValue<Map<NonNullObject, String?>>("""{"foo":null}""")
            assertEquals(mapOf(NonNullObject("foo") to null), result)
        }

        @Test
        fun nullableObject() {
            val result = defaultMapper.readValue<Map<NullableObject, String?>>("""{"bar":null}""")
            assertEquals(mapOf(NullableObject("bar") to null), result)
        }

        @Test
        fun twoUnitPrimitive() {
            val result = defaultMapper.readValue<Map<TwoUnitPrimitive, String?>>("""{"1":null}""")
            assertEquals(mapOf(TwoUnitPrimitive(1.0) to null), result)
        }
    }

    data class Dst(
        val p: Map<Primitive, String?>,
        val nn: Map<NonNullObject, String?>,
        val n: Map<NullableObject, String?>,
        val tup: Map<TwoUnitPrimitive, String?>
    )

    @Test
    fun wrapped() {
        val src = """
            {
              "p":{"1":null},
              "nn":{"foo":null},
              "n":{"bar":null},
              "tup":{"2":null}
            }
        """.trimIndent()
        val result = defaultMapper.readValue<Dst>(src)
        val expected = Dst(
            mapOf(Primitive(1) to null),
            mapOf(NonNullObject("foo") to null),
            mapOf(NullableObject("bar") to null),
            mapOf(TwoUnitPrimitive(2.0) to null)
        )

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
        val e = assertThrows<InvocationTargetException> {
            defaultMapper.readValue<Map<HasCheckConstructor, String?>>("""{"-1":null}""")
        }
        assertTrue(e.cause === throwable)
    }

    data class Wrapped(val first: String, val second: String) {
        class KeyDeserializer : JacksonKeyDeserializer() {
            override fun deserializeKey(key: String, ctxt: DeserializationContext) = key
                .split("-")
                .let { Wrapped(it[0], it[1]) }
        }
    }

    @JvmInline
    value class Wrapper(val w: Wrapped)

    @Test
    fun wrappedCustomObject() {
        // If a type that cannot be deserialized is specified, the default is an error.
        val thrown = assertThrows<JsonMappingException> {
            defaultMapper.readValue<Map<Wrapper, String?>>("""{"foo-bar":null}""")
        }
        assertTrue(thrown.cause is InvalidDefinitionException)

        val mapper = jacksonObjectMapper()
            .registerModule(
                object : SimpleModule() {
                    init {
                        addKeyDeserializer(Wrapped::class.java, Wrapped.KeyDeserializer())
                    }
                }
            )

        val result = mapper.readValue<Map<Wrapper, String?>>("""{"foo-bar":null}""")
        val expected = mapOf(Wrapper(Wrapped("foo", "bar")) to null)

        assertEquals(expected, result)
    }
}
