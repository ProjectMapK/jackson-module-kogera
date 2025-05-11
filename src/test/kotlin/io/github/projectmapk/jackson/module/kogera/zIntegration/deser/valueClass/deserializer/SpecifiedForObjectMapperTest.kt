package io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.deserializer

import com.fasterxml.jackson.databind.module.SimpleModule
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.NonNullObject
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.NullableObject
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.Primitive
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.TwoUnitPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SpecifiedForObjectMapperTest {
    companion object {
        val mapper = jacksonObjectMapper().apply {
            val module = SimpleModule().apply {
                this.addDeserializer(Primitive::class.java, Primitive.Deserializer())
                this.addDeserializer(NonNullObject::class.java, NonNullObject.Deserializer())
                this.addDeserializer(NullableObject::class.java, NullableObject.DeserializerWrapsNullable())
                this.addDeserializer(TwoUnitPrimitive::class.java, TwoUnitPrimitive.Deserializer())
            }
            this.registerModule(module)
        }
    }

    @Nested
    inner class DirectDeserialize {
        @Test
        fun primitive() {
            val result = mapper.readValue<Primitive>("1")
            assertEquals(Primitive(101), result)
        }

        @Test
        fun nonNullObject() {
            val result = mapper.readValue<NonNullObject>(""""foo"""")
            assertEquals(NonNullObject("foo-deser"), result)
        }

        @Suppress("ClassName")
        @Nested
        inner class NullableObject_ {
            @Test
            fun value() {
                val result = mapper.readValue<NullableObject>(""""foo"""")
                assertEquals(NullableObject("foo-deser"), result)
            }

            // failing
            @Test
            fun nullString() {
                val result = mapper.readValue<NullableObject?>("null")
                assertNotEquals(NullableObject("null-value-deser"), result, "#209 has been fixed.")
            }
        }

        @Test
        fun twoUnitPrimitive() {
            val result = mapper.readValue<TwoUnitPrimitive>("1")
            assertEquals(TwoUnitPrimitive(101), result)
        }
    }

    data class Dst(
        val pNn: Primitive,
        val pN: Primitive?,
        val nnoNn: NonNullObject,
        val nnoN: NonNullObject?,
        val noNn: NullableObject,
        val noN: NullableObject?,
        val tupNn: TwoUnitPrimitive,
        val tupN: TwoUnitPrimitive?
    )

    @Test
    fun nonNull() {
        val base = Dst(
            Primitive(1),
            Primitive(2),
            NonNullObject("foo"),
            NonNullObject("bar"),
            NullableObject("baz"),
            NullableObject("qux"),
            TwoUnitPrimitive(3),
            TwoUnitPrimitive(4)
        )
        val src = mapper.writeValueAsString(base)
        val result = mapper.readValue<Dst>(src)

        val expected = Dst(
            Primitive(101),
            Primitive(102),
            NonNullObject("foo-deser"),
            NonNullObject("bar-deser"),
            NullableObject("baz-deser"),
            NullableObject("qux-deser"),
            TwoUnitPrimitive(103),
            TwoUnitPrimitive(104)
        )
        assertEquals(expected, result)
    }

    @Test
    fun withNull() {
        val base = Dst(
            Primitive(1),
            null,
            NonNullObject("foo"),
            null,
            NullableObject(null),
            null,
            TwoUnitPrimitive(3),
            null
        )
        val src = mapper.writeValueAsString(base)
        val result = mapper.readValue<Dst>(src)

        val expected = Dst(
            Primitive(101),
            null,
            NonNullObject("foo-deser"),
            null,
            NullableObject("null-value-deser"),
            null,
            TwoUnitPrimitive(103),
            null
        )
        assertEquals(expected, result)
    }
}
