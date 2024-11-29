package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import io.github.projectmapk.jackson.module.kogera.registerKotlinModule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GitHub841 {
    object Foo {
        override fun toString(): String = "Foo()"

        @JvmStatic
        @JsonCreator
        fun deserialize(): Foo {
            return Foo
        }
    }

    private val mapper = ObjectMapper()
        .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
        .registerKotlinModule()

    @Test
    fun shouldDeserializeSimpleObject() {
        val value = Foo
        val serialized = mapper.writeValueAsString(value)
        val deserialized = mapper.readValue<Foo>(serialized)

        assertEquals(value, deserialized)
    }
}
