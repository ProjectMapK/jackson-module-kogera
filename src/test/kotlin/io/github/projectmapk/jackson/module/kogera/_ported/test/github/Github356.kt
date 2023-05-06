package io.github.projectmapk.jackson.module.kogera._ported.test.github

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestGithub356 {
    private val mapper = jacksonObjectMapper()

    @Test
    fun deserializeInlineClass() {
        assertEquals(
            ClassWithInlineMember(InlineClass("bar")),
            mapper.readValue<ClassWithInlineMember>("""{"inlineClassProperty":"bar"}""")
        )
    }

    @Test
    fun serializeInlineClass() {
        assertEquals(
            """{"inlineClassProperty":"bar"}""",
            mapper.writeValueAsString(ClassWithInlineMember(InlineClass("bar")))
        )
    }

    @Test
    fun deserializeValueClass() {
        assertEquals(
            ClassWithValueMember(ValueClass("bar")),
            mapper.readValue<ClassWithValueMember>("""{"valueClassProperty":"bar"}""")
        )
    }

    @Test
    fun serializeValueClass() {
        assertEquals(
            """{"valueClassProperty":"bar"}""",
            mapper.writeValueAsString(ClassWithValueMember(ValueClass("bar")))
        )
    }
}

// Deprecated usage kept at 1.5.0 upgrade; delete in the future
inline class InlineClass(val value: String)

@JsonDeserialize(builder = ClassWithInlineMember.JacksonBuilder::class)
data class ClassWithInlineMember(val inlineClassProperty: InlineClass) {
    data class JacksonBuilder constructor(val inlineClassProperty: String) {
        fun build() = ClassWithInlineMember(InlineClass(inlineClassProperty))
    }
}

@JvmInline
value class ValueClass(val value: String)

@JsonDeserialize(builder = ClassWithValueMember.JacksonBuilder::class)
data class ClassWithValueMember(val valueClassProperty: ValueClass) {
    data class JacksonBuilder constructor(val valueClassProperty: String) {
        fun build() = ClassWithValueMember(ValueClass(valueClassProperty))
    }
}
