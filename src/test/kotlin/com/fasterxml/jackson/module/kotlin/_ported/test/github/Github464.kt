package com.fasterxml.jackson.module.kotlin._ported.test.github

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.testPrettyWriter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Github464 {
    object NullValueClassKeySerializer : StdSerializer<ValueClass>(ValueClass::class.java) {
        override fun serialize(value: ValueClass?, gen: JsonGenerator, provider: SerializerProvider) {
            gen.writeFieldName("null-key")
        }
    }

    interface IValue

    @JvmInline
    value class ValueClass(val value: Int?) : IValue
    data class WrapperClass(val inlineField: ValueClass)

    abstract class AbstractGetter<T> {
        abstract val qux: T

        val plugh: T get() = qux
    }
    interface IGetter<T> {
        val quux: T

        val xyzzy: T get() = quux
    }

    class Poko(
        val foo: ValueClass,
        val bar: ValueClass?,
        val baz: IValue,
        override val qux: ValueClass,
        override val quux: ValueClass,
        val corge: Collection<ValueClass?>,
        val grault: Array<ValueClass?>,
        val garply: WrapperClass,
        val waldo: WrapperClass?,
        val fred: Map<ValueClass?, ValueClass?>
    ) : AbstractGetter<ValueClass>(), IGetter<ValueClass>

    object NullValueSerializer : StdSerializer<Any>(Any::class.java) {
        override fun serialize(value: Any?, gen: JsonGenerator, provider: SerializerProvider) {
            gen.writeString("null-value")
        }
    }

    @Nested
    inner class UnboxTest {

        private val zeroValue = ValueClass(0)
        private val oneValue = ValueClass(1)
        private val nullValue = ValueClass(null)

        private val target = Poko(
            foo = zeroValue,
            bar = null,
            baz = zeroValue,
            qux = zeroValue,
            quux = zeroValue,
            corge = listOf(zeroValue, null),
            grault = arrayOf(zeroValue, null),
            garply = WrapperClass(zeroValue),
            waldo = null,
            fred = mapOf(zeroValue to zeroValue, oneValue to null, nullValue to nullValue, null to null)
        )

        @Test
        fun test() {
            @Suppress("UNCHECKED_CAST")
            val writer: ObjectWriter = jacksonObjectMapper()
                .apply { serializerProvider.setNullKeySerializer(NullValueClassKeySerializer as JsonSerializer<Any?>) }
                .testPrettyWriter()

            assertEquals(
                """
                    {
                      "foo" : {
                        "value" : 0
                      },
                      "bar" : null,
                      "baz" : {
                        "value" : 0
                      },
                      "qux" : {
                        "value" : 0
                      },
                      "quux" : {
                        "value" : 0
                      },
                      "corge" : [ {
                        "value" : 0
                      }, null ],
                      "grault" : [ {
                        "value" : 0
                      }, null ],
                      "garply" : {
                        "inlineField" : {
                          "value" : 0
                        }
                      },
                      "waldo" : null,
                      "fred" : {
                        "ValueClass(value=0)" : {
                          "value" : 0
                        },
                        "ValueClass(value=1)" : null,
                        "ValueClass(value=null)" : {
                          "value" : null
                        },
                        "null-key" : null
                      },
                      "xyzzy" : {
                        "value" : 0
                      },
                      "plugh" : {
                        "value" : 0
                      }
                    }
                """.trimIndent(),
                writer.writeValueAsString(target)
            )
        }

        @Test
        fun nullValueSerializerTest() {
            @Suppress("UNCHECKED_CAST")
            val writer = jacksonObjectMapper()
                .apply {
                    serializerProvider.setNullKeySerializer(NullValueClassKeySerializer as JsonSerializer<Any?>)
                    serializerProvider.setNullValueSerializer(NullValueSerializer)
                }.testPrettyWriter()

            assertEquals(
                """
                    {
                      "foo" : {
                        "value" : 0
                      },
                      "bar" : "null-value",
                      "baz" : {
                        "value" : 0
                      },
                      "qux" : {
                        "value" : 0
                      },
                      "quux" : {
                        "value" : 0
                      },
                      "corge" : [ {
                        "value" : 0
                      }, "null-value" ],
                      "grault" : [ {
                        "value" : 0
                      }, "null-value" ],
                      "garply" : {
                        "inlineField" : {
                          "value" : 0
                        }
                      },
                      "waldo" : "null-value",
                      "fred" : {
                        "ValueClass(value=0)" : {
                          "value" : 0
                        },
                        "ValueClass(value=1)" : "null-value",
                        "ValueClass(value=null)" : {
                          "value" : "null-value"
                        },
                        "null-key" : "null-value"
                      },
                      "xyzzy" : {
                        "value" : 0
                      },
                      "plugh" : {
                        "value" : 0
                      }
                    }
                """.trimIndent(),
                writer.writeValueAsString(target)
            )
        }
    }

    class SerializerPriorityTest {
        @JvmInline
        value class ValueBySerializer(val value: Int)

        object Serializer : StdSerializer<ValueBySerializer>(ValueBySerializer::class.java) {
            override fun serialize(value: ValueBySerializer, gen: JsonGenerator, provider: SerializerProvider) {
                gen.writeString(value.value.toString())
            }
        }
        object KeySerializer : StdSerializer<ValueBySerializer>(ValueBySerializer::class.java) {
            override fun serialize(value: ValueBySerializer, gen: JsonGenerator, provider: SerializerProvider) {
                gen.writeFieldName(value.value.toString())
            }
        }

        private val target = mapOf(ValueBySerializer(1) to ValueBySerializer(2))
        private val sm = SimpleModule()
            .addSerializer(Serializer)
            .addKeySerializer(ValueBySerializer::class.java, KeySerializer)

        @Test
        fun simpleTest() {
            val om: ObjectMapper = jacksonMapperBuilder().addModule(sm).build()

            assertEquals("""{"1":"2"}""", om.writeValueAsString(target))
        }

        // Currently, there is a situation where the serialization results are different depending on the registration order of the modules.
        // This problem is not addressed because the serializer registered by the user has priority over Extensions.kt,
        // since KotlinModule is basically registered first.
        @Disabled
        @Test
        fun priorityTest() {
            val km = KotlinModule.Builder().build()
            val om1: ObjectMapper = JsonMapper.builder().addModules(km, sm).build()
            val om2: ObjectMapper = JsonMapper.builder().addModules(sm, km).build()

            // om1(collect) -> """{"1":"2"}"""
            // om2(broken)  -> """{"1":2}"""
            assertEquals(om1.writeValueAsString(target), om2.writeValueAsString(target))
        }
    }
}
