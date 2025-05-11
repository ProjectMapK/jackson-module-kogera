package io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.defaultArgument

import com.fasterxml.jackson.annotation.JsonCreator
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.TwoUnitPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TwoUnitPrimitiveTest {
    data class ByConstructor(
        val nn: TwoUnitPrimitive = TwoUnitPrimitive(1),
        val nNn: TwoUnitPrimitive? = TwoUnitPrimitive(2),
        val nN: TwoUnitPrimitive? = null
    )

    @Test
    fun byConstructorTest() {
        assertEquals(ByConstructor(), defaultMapper.readValue<ByConstructor>("{}"))
    }

    data class ByFactory(val nn: TwoUnitPrimitive, val nNn: TwoUnitPrimitive?, val nN: TwoUnitPrimitive?) {
        companion object {
            @JvmStatic
            @JsonCreator
            fun creator(
                nn: TwoUnitPrimitive = TwoUnitPrimitive(1),
                nNn: TwoUnitPrimitive? = TwoUnitPrimitive(2),
                nN: TwoUnitPrimitive? = null
            ) = ByFactory(nn, nNn, nN)
        }
    }

    @Test
    fun byFactoryTest() {
        assertEquals(ByFactory.creator(), defaultMapper.readValue<ByFactory>("{}"))
    }
}
