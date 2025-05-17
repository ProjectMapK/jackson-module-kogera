package io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.defaultArgument

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.exc.ValueInstantiationException
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.NullablePrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class NullablePrimitiveTest {
    data class ByConstructor(
        val nnNn: NullablePrimitive = NullablePrimitive(1),
        val nnN: NullablePrimitive = NullablePrimitive(null),
        val nNn: NullablePrimitive? = NullablePrimitive(2),
        val nN: NullablePrimitive? = null,
    )

    @Test
    fun byConstructorTestFailing() {
        val ex = assertThrows<ValueInstantiationException>("Kogera #51 fixed") {
            assertEquals(ByConstructor(), defaultMapper.readValue<ByConstructor>("{}"))
        }
        assertTrue(ex.cause is NoSuchMethodException)
    }

    data class ByFactory(
        val nnNn: NullablePrimitive = NullablePrimitive(1),
        val nnN: NullablePrimitive = NullablePrimitive(null),
        val nNn: NullablePrimitive? = NullablePrimitive(2),
        val nN: NullablePrimitive? = null,
    ) {
        companion object {
            @JvmStatic
            @JsonCreator
            fun creator(
                nnNn: NullablePrimitive = NullablePrimitive(1),
                nnN: NullablePrimitive = NullablePrimitive(null),
                nNn: NullablePrimitive? = NullablePrimitive(2),
                nN: NullablePrimitive? = null,
            ) = ByFactory(nnNn, nnN, nNn, nN)
        }
    }

    @Test
    fun byFactoryTest() {
        val ex = assertThrows<ValueInstantiationException>("Kogera #51 fixed") {
            assertEquals(ByFactory.creator(), defaultMapper.readValue<ByFactory>("{}"))
        }
        assertTrue(ex.cause is NoSuchMethodException)
    }
}
