package io.github.projectmapk.jackson.module.kogera._integration.deser.value_class.default_argument

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.exc.ValueInstantiationException
import io.github.projectmapk.jackson.module.kogera._integration.deser.value_class.NullableObject
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class NullableObjectTest {
    companion object {
        val mapper = jacksonObjectMapper()
    }

    data class ByConstructor(
        val nnNn: NullableObject = NullableObject("foo"),
        val nnN: NullableObject = NullableObject(null),
        val nNn: NullableObject? = NullableObject("bar"),
        val nN: NullableObject? = null
    )

    @Test
    fun byConstructorTestFailing() {
        val ex = assertThrows<ValueInstantiationException>("Kogera #51 fixed") {
            assertEquals(ByConstructor(), mapper.readValue<ByConstructor>("{}"))
        }
        assertTrue(ex.cause is NoSuchMethodException)
    }

    data class ByFactory(
        val nnNn: NullableObject = NullableObject("foo"),
        val nnN: NullableObject = NullableObject(null),
        val nNn: NullableObject? = NullableObject("bar"),
        val nN: NullableObject? = null
    ) {
        companion object {
            @JvmStatic
            @JsonCreator
            fun creator(
                nn: NullableObject = NullableObject("foo"),
                nnN: NullableObject = NullableObject(null),
                nNn: NullableObject? = NullableObject("bar"),
                nN: NullableObject? = null
            ) = ByFactory(nn, nnN, nNn, nN)
        }
    }

    @Test
    fun byFactoryTest() {
        val ex = assertThrows<ValueInstantiationException>("Kogera #51 fixed") {
            assertEquals(ByFactory.creator(), mapper.readValue<ByFactory>("{}"))
        }
        assertTrue(ex.cause is NoSuchMethodException)
    }
}
