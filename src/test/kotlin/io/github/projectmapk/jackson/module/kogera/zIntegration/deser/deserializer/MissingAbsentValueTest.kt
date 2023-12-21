package io.github.projectmapk.jackson.module.kogera.zIntegration.deser.deserializer

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MissingAbsentValueTest {
    class Deser : StdDeserializer<Int>(Int::class.java) {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Int {
            TODO("Not yet implemented")
        }

        override fun getAbsentValue(ctxt: DeserializationContext) = -1
    }

    data class D(
        @JsonDeserialize(using = Deser::class) val foo: Int,
        @JsonDeserialize(using = Deser::class) val bar: Int?
    )

    @Test
    fun test() {
        val mapper = jacksonObjectMapper()
        val result = mapper.readValue<D>("{}")

        assertEquals(D(-1, -1), result)
    }
}
