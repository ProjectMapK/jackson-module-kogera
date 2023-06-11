package io.github.projectmapk.jackson.module.kogera._integration.deser.value_class.default_argument

import com.fasterxml.jackson.annotation.JsonCreator
import io.github.projectmapk.jackson.module.kogera._integration.deser.value_class.NonNullObject
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NonNullObjectTest {
    companion object {
        val mapper = jacksonObjectMapper()
    }

    data class ByConstructor(
        val nn: NonNullObject = NonNullObject("foo"),
        val nNn: NonNullObject? = NonNullObject("bar"),
        val nN: NonNullObject? = null
    )

    @Test
    fun byConstructorTest() {
        assertEquals(ByConstructor(), mapper.readValue<ByConstructor>("{}"))
    }

    data class ByFactory(val nn: NonNullObject, val nNn: NonNullObject?, val nN: NonNullObject?) {
        companion object {
            @JvmStatic
            @JsonCreator
            fun creator(
                nn: NonNullObject = NonNullObject("foo"),
                nNn: NonNullObject? = NonNullObject("bar"),
                nN: NonNullObject? = null
            ) = ByFactory(nn, nNn, nN)
        }
    }

    @Test
    fun byFactoryTest() {
        assertEquals(ByFactory.creator(), mapper.readValue<ByFactory>("{}"))
    }
}
