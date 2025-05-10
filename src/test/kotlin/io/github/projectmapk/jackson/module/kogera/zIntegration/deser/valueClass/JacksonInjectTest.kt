package io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.databind.InjectableValues
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class JacksonInjectTest {
    // This is specified as a getter because there is a possibility of problems if it is assigned to a field.
    // see: https://github.com/FasterXML/jackson-databind/issues/4218
    data class Dto(
        @get:JacksonInject("pNn")
        val pNn: Primitive,
        @get:JacksonInject("pN")
        val pN: Primitive?,
        @get:JacksonInject("nnoNn")
        val nnoNn: NonNullObject,
        @get:JacksonInject("nnoN")
        val nnoN: NonNullObject?,
        @get:JacksonInject("noNnNn")
        val noNnNn: NullableObject,
        @get:JacksonInject("noNnN")
        val noNnN: NullableObject,
        @get:JacksonInject("noNNn")
        val noNNn: NullableObject?,
        @get:JacksonInject("noNN")
        val noNN: NullableObject?,
        @get:JacksonInject("tupNn")
        val tupNn: TwoUnitPrimitive,
        @get:JacksonInject("tupN")
        val tupN: TwoUnitPrimitive?
    )

    @Test
    fun test() {
        val injectables = mapOf(
            "pNn" to Primitive(0),
            "pN" to Primitive(1),
            "nnoNn" to NonNullObject("nnoNn"),
            "nnoN" to NonNullObject("nnoN"),
            "noNnNn" to NullableObject("noNnNn"),
            "noNnN" to NullableObject(null),
            "noNNn" to NullableObject("noNNn"),
            "noNN" to NullableObject(null),
            "tupNn" to TwoUnitPrimitive(3.0),
            "tupN" to TwoUnitPrimitive(4.0)
        )

        val reader = jacksonObjectMapper()
            .readerFor(Dto::class.java)
            .with(InjectableValues.Std(injectables))

        val result = reader.readValue<Dto>("{}")
        val expected = ::Dto.let { ctor ->
            val args = ctor.parameters.associateWith { injectables[it.name] }
            ctor.callBy(args)
        }

        assertEquals(expected, result)
    }

    data class DataBind4218FailingDto(
        @field:JacksonInject("pNn")
        val pNn: Primitive,
        @field:JacksonInject("pN")
        val pN: Primitive?
    )

    // remove if fixed
    @Test
    fun dataBind4218Failing() {
        val injectables = InjectableValues.Std(mapOf("pNn" to Primitive(0), "pN" to Primitive(1)))

        val reader = jacksonObjectMapper()
            .readerFor(DataBind4218FailingDto::class.java)
            .with(injectables)

        val ex = assertThrows<IllegalArgumentException> { reader.readValue<DataBind4218FailingDto>("{}") }
        assertEquals(
            "Can not set final int field io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.JacksonInjectTest\$DataBind4218FailingDto.pNn to io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.Primitive",
            ex.message
        )
    }
}
