package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestGithub356 {
    private val mapper = jacksonObjectMapper()

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

@JvmInline
value class ValueClass(val value: String)

@JsonDeserialize(builder = ClassWithValueMember.JacksonBuilder::class)
data class ClassWithValueMember(val valueClassProperty: ValueClass) {
    data class JacksonBuilder constructor(val valueClassProperty: String) {
        fun build() = ClassWithValueMember(ValueClass(valueClassProperty))
    }
}
