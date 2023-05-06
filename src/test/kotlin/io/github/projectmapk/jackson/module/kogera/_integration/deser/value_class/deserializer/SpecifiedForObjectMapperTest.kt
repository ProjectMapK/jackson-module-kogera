package io.github.projectmapk.jackson.module.kogera._integration.deser.value_class.deserializer

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.module.SimpleModule
import io.github.projectmapk.jackson.module.kogera._integration.deser.value_class.NonNullObject
import io.github.projectmapk.jackson.module.kogera._integration.deser.value_class.NullableObject
import io.github.projectmapk.jackson.module.kogera._integration.deser.value_class.Primitive
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SpecifiedForObjectMapperTest {
    companion object {
        val mapper = jacksonObjectMapper().apply {
            val module = SimpleModule().apply {
                this.addDeserializer(Primitive::class.java, Primitive.Deserializer())
                this.addDeserializer(NonNullObject::class.java, NonNullObject.Deserializer())
                this.addDeserializer(NullableObject::class.java, NullableObject.Deserializer())
            }
            this.registerModule(module)
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
    fun nonNull() {
        val base = Dst(
            Primitive(1),
            Primitive(2),
            NonNullObject("foo"),
            NonNullObject("bar"),
            NullableObject("baz"),
            NullableObject("qux")
        )
        val src = mapper.writeValueAsString(base)
        val result = mapper.readValue<Dst>(src)

        val expected = Dst(
            Primitive(101),
            Primitive(102),
            NonNullObject("foo-deser"),
            NonNullObject("bar-deser"),
            NullableObject("baz-deser"),
            NullableObject("qux-deser")
        )
        assertEquals(expected, result)
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
        val base = WithoutNoNn(
            Primitive(1),
            null,
            NonNullObject("foo"),
            null,
            // NullableObject(null),
            null
        )
        val src = mapper.writeValueAsString(base)
        val result = mapper.readValue<WithoutNoNn>(src)

        val expected = WithoutNoNn(
            Primitive(101),
            null,
            NonNullObject("foo-deser"),
            null,
            // NullableObject(null),
            null
        )
        assertEquals(expected, result)
    }

    data class Failing(val noNn: NullableObject)

    @Test
    fun failing() {
        val expected = Failing(NullableObject(null))
        val src = mapper.writeValueAsString(expected)

        assertThrows<MismatchedInputException>("Kogera #42 is fixed") {
            val result = mapper.readValue<Failing>(src)
            assertEquals(expected, result)
        }
    }
    // endregion
}
