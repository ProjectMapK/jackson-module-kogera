package io.github.projectmapk.jackson.module.kogera.zPorted.test

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestSequenceDeserializer {
    data class Data(val value: Sequence<String>)

    @Test
    fun deserializeSequence() {
        val list = listOf("Test", "Test1")
        val result = defaultMapper.readValue<Data>("{\"value\":[\"Test\",\"Test1\"]}")
        assertEquals(list, result.value.toList())
    }

    @Test
    fun deserializeEmptySequence() {
        val list = listOf<String>()
        val result = defaultMapper.readValue<Data>("{\"value\":[]}")
        assertEquals(list, result.value.toList())
    }

    @Test
    fun testSerializeSequence() {
        val sequence = listOf("item1", "item2").asSequence()
        val data = Data(sequence)
        val result = defaultMapper.writeValueAsString(data)
        assertEquals("{\"value\":[\"item1\",\"item2\"]}", result)
    }

    @Test
    fun testSerializeEmptySequence() {
        val sequence = listOf<String>().asSequence()
        val data = Data(sequence)
        val result = defaultMapper.writeValueAsString(data)
        assertEquals("{\"value\":[]}", result)
    }

    class ContentSer : StdSerializer<String>(String::class.java) {
        override fun serialize(value: String, gen: JsonGenerator, provider: SerializerProvider) {
            provider.defaultSerializeValue("$value-ser", gen)
        }
    }

    data class ListWrapper(
        @JsonSerialize(contentUsing = ContentSer::class) val value: List<String>
    )

    data class SequenceWrapper(
        @JsonSerialize(contentUsing = ContentSer::class)
        val value: Sequence<String>
    )

    @Test
    fun contentUsingTest() {
        val listResult = defaultMapper.writeValueAsString(ListWrapper(listOf("foo")))
        val sequenceResult = defaultMapper.writeValueAsString(SequenceWrapper(sequenceOf("foo")))

        assertEquals("""{"value":["foo-ser"]}""", sequenceResult)
        assertEquals(listResult, sequenceResult)
    }

    // @see #674
    @Test
    fun sequenceOfTest() {
        val result = defaultMapper.writeValueAsString(sequenceOf("foo"))

        assertEquals("""["foo"]""", result)
    }
}
