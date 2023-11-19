package io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.jsonCreator

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Test on the case of deserialization by ValueClassBoxDeserializer
class ValueClassByOriginalCreatorTest {
    @JvmInline
    value class Primary(val primary: String)

    @JvmInline
    value class SpecifiedPrimary @JsonCreator constructor(val primary: String?)

    @JvmInline
    value class Secondary(val value: String) {
        @JsonCreator constructor(value: Int) : this("$value-creator")
    }

    @JvmInline
    value class Factory(val value: Int) {
        companion object {
            @JvmStatic
            @JsonCreator
            fun creator(value: Int): Factory = Factory(value + 100)
        }
    }

    @Test
    fun directDeserTest() {
        val mapper = jacksonObjectMapper()

        assertEquals(Primary("a"), mapper.readValue<Primary>("\"a\""))
        assertEquals(SpecifiedPrimary("b"), mapper.readValue<SpecifiedPrimary>("\"b\""))
        assertEquals(Secondary("1-creator"), mapper.readValue<Secondary>("1"))
        assertEquals(Factory(101), mapper.readValue<Factory>("1"))
    }

    data class Dst(
        val foo: Primary,
        val bar: SpecifiedPrimary,
        val baz: Secondary,
        val qux: Factory
    )

    @Test
    fun parameterTest() {
        val mapper = jacksonObjectMapper()

        val r = mapper.readValue<Dst>(
            """
            {
              "foo":"a",
              "bar":"b",
              "baz":1,
              "qux":1
            }
            """.trimIndent()
        )

        assertEquals(
            Dst(
                Primary("a"),
                SpecifiedPrimary("b"),
                Secondary("1-creator"),
                Factory(101)
            ),
            r
        )
    }

    @JvmInline
    value class MultipleValueConstructor(val value: String) {
        @JsonCreator constructor(v1: String, v2: String) : this(v1 + v2)
    }

    @JvmInline
    value class MultipleValueFactory(val value: Int) {
        companion object {
            @JsonCreator
            @JvmStatic
            fun creator(v1: Int, v2: Int): MultipleValueFactory = MultipleValueFactory(v1 + v2)
        }
    }

    // A Creator that requires multiple arguments is basically an error.
    @Test
    fun handleErrorTest() {
        val mapper = jacksonObjectMapper()

        assertThrows<InvalidDefinitionException> {
            mapper.readValue<MultipleValueConstructor>("""{"v1":"1","v2":"2"}""")
        }
        assertThrows<InvalidDefinitionException> {
            mapper.readValue<MultipleValueFactory>("""{"v1":1,"v2":2}""")
        }
    }
}
