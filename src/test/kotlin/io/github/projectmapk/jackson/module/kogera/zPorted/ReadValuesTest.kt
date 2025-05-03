package io.github.projectmapk.jackson.module.kogera.zPorted

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.RuntimeJsonMappingException
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValues
import io.github.projectmapk.jackson.module.kogera.readValuesTyped
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ReadValuesTest {
    class MyStrDeser : StdDeserializer<String>(String::class.java) {
        override fun deserialize(
            p: JsonParser,
            ctxt: DeserializationContext
        ): String? = p.valueAsString.takeIf { it != "bar" }
    }

    @Nested
    inner class CheckTypeMismatchTest {
        val mapper = jacksonObjectMapper().registerModule(
            object : SimpleModule() {
                init {
                    addDeserializer(String::class.java, MyStrDeser())
                }
            }
        )!!

        @Test
        fun readValuesJsonParserNext() {
            val src = mapper.createParser(""""foo"${"\n"}"bar"""")
            val itr = mapper.readValues<String>(src)

            assertEquals("foo", itr.next())
            assertThrows<RuntimeJsonMappingException> {
                itr.next()
            }
        }

        @Test
        fun readValuesJsonParserNextValue() {
            val src = mapper.createParser(""""foo"${"\n"}"bar"""")
            val itr = mapper.readValues<String>(src)

            assertEquals("foo", itr.nextValue())
            assertThrows<RuntimeJsonMappingException> {
                itr.nextValue()
            }
        }

        @Test
        fun readValuesTypedJsonParser() {
            val reader = mapper.reader()
            val src = reader.createParser(""""foo"${"\n"}"bar"""")
            val itr = reader.readValuesTyped<String>(src)

            assertEquals("foo", itr.next())
            assertThrows<RuntimeJsonMappingException> {
                itr.next()
            }
        }
    }
}
