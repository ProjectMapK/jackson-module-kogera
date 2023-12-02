package io.github.projectmapk.jackson.module.kogera.zIntegration.deser

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

private class FailNullForPrimitiveTest {
    data class Dto(
        val foo: Int,
        val bar: Int?
    )

    @Test
    fun test() {
        val mapper = jacksonObjectMapper()
            .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)

        assertThrows<MismatchedInputException> {
            mapper.readValue<Dto>("{}")
        }

        assertThrows<MismatchedInputException> {
            mapper.readValue<Dto>("""{"foo":null}""")
        }

        assertEquals(Dto(0, null), mapper.readValue<Dto>("""{"foo":0}"""))
    }
}
