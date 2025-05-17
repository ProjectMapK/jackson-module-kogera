package io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.mapKey.keyDeserializer

import com.fasterxml.jackson.databind.module.SimpleModule
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.NonNullObject
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.NullableObject
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.NullablePrimitive
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.Primitive
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.TwoUnitPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SpecifiedForObjectMapperTest {
    companion object {
        val mapper = jacksonObjectMapper().apply {
            val module = SimpleModule().apply {
                this.addKeyDeserializer(Primitive::class.java, Primitive.KeyDeserializer())
                this.addKeyDeserializer(NonNullObject::class.java, NonNullObject.KeyDeserializer())
                this.addKeyDeserializer(NullableObject::class.java, NullableObject.KeyDeserializer())
                this.addKeyDeserializer(NullablePrimitive::class.java, NullablePrimitive.KeyDeserializer())
                this.addKeyDeserializer(TwoUnitPrimitive::class.java, TwoUnitPrimitive.KeyDeserializer())
            }
            this.registerModule(module)
        }
    }

    @Nested
    inner class DirectDeserialize {
        @Test
        fun primitive() {
            val result = mapper.readValue<Map<Primitive, String?>>("""{"1":null}""")
            assertEquals(mapOf(Primitive(101) to null), result)
        }

        @Test
        fun nonNullObject() {
            val result = mapper.readValue<Map<NonNullObject, String?>>("""{"foo":null}""")
            assertEquals(mapOf(NonNullObject("foo-deser") to null), result)
        }

        @Test
        fun nullableObject() {
            val result = mapper.readValue<Map<NullableObject, String?>>("""{"bar":null}""")
            assertEquals(mapOf(NullableObject("bar-deser") to null), result)
        }

        @Test
        fun nullablePrimitive() {
            val result = mapper.readValue<Map<NullablePrimitive, String?>>("""{"2":null}""")
            assertEquals(mapOf(NullablePrimitive(102) to null), result)
        }

        @Test
        fun twoUnitPrimitive() {
            val result = mapper.readValue<Map<TwoUnitPrimitive, String?>>("""{"1":null}""")
            assertEquals(mapOf(TwoUnitPrimitive(101) to null), result)
        }
    }

    data class Dst(
        val p: Map<Primitive, String?>,
        val nn: Map<NonNullObject, String?>,
        val n: Map<NullableObject, String?>,
        val np: Map<NullablePrimitive, String?>,
        val tup: Map<TwoUnitPrimitive, String?>
    )

    @Test
    fun wrapped() {
        val src = """
            {
              "p":{"1":null},
              "nn":{"foo":null},
              "n":{"bar":null},
              "np":{"2":null},
              "tup":{"1":null}
            }
        """.trimIndent()
        val result = mapper.readValue<Dst>(src)
        val expected = Dst(
            mapOf(Primitive(101) to null),
            mapOf(NonNullObject("foo-deser") to null),
            mapOf(NullableObject("bar-deser") to null),
            mapOf(NullablePrimitive(102) to null),
            mapOf(TwoUnitPrimitive(101) to null)
        )

        assertEquals(expected, result)
    }
}
