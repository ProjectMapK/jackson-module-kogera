package io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.jsonCreator

import com.fasterxml.jackson.annotation.JsonCreator
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.NonNullObject
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.NullableObject
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.Primitive
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.TwoUnitPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private fun Primitive.modify(): Primitive = Primitive(v + 100)
private fun NonNullObject.modify(): NonNullObject = NonNullObject("$v-creator")
private fun NullableObject.modify(): NullableObject = NullableObject(v!! + "-creator")
private fun TwoUnitPrimitive.modify(): TwoUnitPrimitive = TwoUnitPrimitive(v + 100)

class InCreatorArgumentTest {
    data class Dst(
        val pNn: Primitive,
        val pN: Primitive?,
        val nnoNn: NonNullObject,
        val nnoN: NonNullObject?,
        val noNn: NullableObject,
        val noN: NullableObject?,
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
                tupNn: TwoUnitPrimitive,
                tupN: TwoUnitPrimitive?
            ) = Dst(
                pNn.modify(),
                pN?.modify(),
                nnoNn.modify(),
                nnoN?.modify(),
                noNn.modify(),
                noN?.modify(),
                tupNn.modify(),
                tupN?.modify()
            )
        }
    }

    @Test
    fun test() {
        val mapper = jacksonObjectMapper()
        val base = Dst(
            Primitive(1),
            Primitive(2),
            NonNullObject("nnoNn"),
            NonNullObject("nnoN"),
            NullableObject("noNn"),
            NullableObject("noN"),
            TwoUnitPrimitive(3.0),
            TwoUnitPrimitive(4.0)
        )
        val result = mapper.readValue<Dst>(mapper.writeValueAsString(base))

        assertEquals(
            base.copy(
                pNn = base.pNn.modify(),
                pN = base.pN?.modify(),
                nnoNn = base.nnoNn.modify(),
                nnoN = base.nnoN?.modify(),
                noNn = base.noNn.modify(),
                noN = base.noN?.modify(),
                tupNn = base.tupNn.modify(),
                tupN = base.tupN?.modify()
            ),
            result
        )
    }
}
