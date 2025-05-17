package io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.jsonCreator

import com.fasterxml.jackson.annotation.JsonCreator
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.NonNullObject
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.NullableObject
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.NullablePrimitive
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.Primitive
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.TwoUnitPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private fun Primitive.modify(): Primitive = Primitive(v + 100)
private fun NonNullObject.modify(): NonNullObject = NonNullObject("$v-creator")
private fun NullableObject.modify(): NullableObject = NullableObject(v!! + "-creator")
private fun NullablePrimitive.modify(): NullablePrimitive = NullablePrimitive(v!! + 100)
private fun TwoUnitPrimitive.modify(): TwoUnitPrimitive = TwoUnitPrimitive(v + 100)

class InCreatorArgumentTest {
    data class Dst(
        val pNn: Primitive,
        val pN: Primitive?,
        val nnoNn: NonNullObject,
        val nnoN: NonNullObject?,
        val noNn: NullableObject,
        val noN: NullableObject?,
        val npNn: NullablePrimitive,
        val npN: NullablePrimitive?,
        val tupNn: TwoUnitPrimitive,
        val tupN: TwoUnitPrimitive?
    ) {
        companion object {
            @JvmStatic
            @JsonCreator
            fun creator(
                pNn: Primitive,
                pN: Primitive?,
                nnoNn: NonNullObject,
                nnoN: NonNullObject?,
                noNn: NullableObject,
                noN: NullableObject?,
                npNn: NullablePrimitive,
                npN: NullablePrimitive?,
                tupNn: TwoUnitPrimitive,
                tupN: TwoUnitPrimitive?
            ) = Dst(
                pNn.modify(),
                pN?.modify(),
                nnoNn.modify(),
                nnoN?.modify(),
                noNn.modify(),
                noN?.modify(),
                npNn.modify(),
                npN?.modify(),
                tupNn.modify(),
                tupN?.modify()
            )
        }
    }

    @Test
    fun test() {
        val base = Dst(
            Primitive(1),
            Primitive(2),
            NonNullObject("nnoNn"),
            NonNullObject("nnoN"),
            NullableObject("noNn"),
            NullableObject("noN"),
            NullablePrimitive(3),
            NullablePrimitive(4),
            TwoUnitPrimitive(5),
            TwoUnitPrimitive(6)
        )
        val result = defaultMapper.readValue<Dst>(defaultMapper.writeValueAsString(base))

        assertEquals(
            base.copy(
                pNn = base.pNn.modify(),
                pN = base.pN?.modify(),
                nnoNn = base.nnoNn.modify(),
                nnoN = base.nnoN?.modify(),
                noNn = base.noNn.modify(),
                noN = base.noN?.modify(),
                npNn = base.npNn.modify(),
                npN = base.npN?.modify(),
                tupNn = base.tupNn.modify(),
                tupN = base.tupN?.modify()
            ),
            result
        )
    }
}
