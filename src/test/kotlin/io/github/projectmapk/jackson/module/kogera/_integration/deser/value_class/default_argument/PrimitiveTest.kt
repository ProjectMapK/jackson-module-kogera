package io.github.projectmapk.jackson.module.kogera._integration.deser.value_class.default_argument

import com.fasterxml.jackson.annotation.JsonCreator
import io.github.projectmapk.jackson.module.kogera._integration.deser.value_class.Primitive
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PrimitiveTest {
    companion object {
        val mapper = jacksonObjectMapper()
    }

    data class ByConstructor(
        val nn: Primitive = Primitive(1),
        val nNn: Primitive? = Primitive(2),
        val nN: Primitive? = null
    )

    @Test
    fun byConstructorTest() {
        assertEquals(ByConstructor(), mapper.readValue<ByConstructor>("{}"))
    }

    data class ByFactory(val nn: Primitive, val nNn: Primitive?, val nN: Primitive?) {
        companion object {
            @JvmStatic
            @JsonCreator
            fun creator(
                nn: Primitive = Primitive(1),
                nNn: Primitive? = Primitive(2),
                nN: Primitive? = null
            ) = ByFactory(nn, nNn, nN)
        }
    }

    @Test
    fun byFactoryTest() {
        assertEquals(ByFactory.creator(), mapper.readValue<ByFactory>("{}"))
    }
}
