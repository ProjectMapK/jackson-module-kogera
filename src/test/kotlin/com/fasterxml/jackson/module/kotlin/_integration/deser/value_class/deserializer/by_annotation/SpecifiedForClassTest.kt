package com.fasterxml.jackson.module.kotlin._integration.deser.value_class.deserializer.by_annotation

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SpecifiedForClassTest {
    @JsonDeserialize(using = Value.Deserializer::class)
    @JvmInline
    value class Value(val v: Int) {
        class Deserializer : StdDeserializer<Value>(Value::class.java) {
            override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Value = Value(p.intValue + 100)
        }
    }

    @Test
    fun directDeserTest() {
        val mapper = jacksonObjectMapper()
        val result = mapper.readValue<Value>("1")

        assertEquals(Value(101), result)
    }

    data class Wrapper(val v: Value)

    @Test
    fun paramDeserTest() {
        val mapper = jacksonObjectMapper()
        val result = mapper.readValue<Wrapper>("""{"v":1}""")

        assertEquals(Wrapper(Value(101)), result)
    }
}
