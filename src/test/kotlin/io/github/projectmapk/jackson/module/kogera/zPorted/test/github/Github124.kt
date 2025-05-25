package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Test

class TestGithub124 {
    // test for [module-kotlin#124]: broken in 2.9.3, fixed in 2.9.6
    @Test
    fun test() {
        // with 2.9.3 prints
        //     Foo(name=foo, query=NonSerializable, rawQuery=bar)
        // but with 2.9.4 throws:
        //     io.github.projectmapk.jackson.module.kogera.MissingKotlinParameterException: Instantiation of [simple type, class DeserializationTest$Foo] value failed for JSON property query due to missing (therefore NULL) value for creator parameter rawQuery which is a non-nullable type
        //      at [Source: (String)"{"name": "foo", "query": "bar"}"; line: 1, column: 31] (through reference chain: DeserializationTest$Foo["query"])
        val deserialized: Foo = defaultMapper.readValue("{\"name\": \"foo\", \"query\": \"bar\"}")
        println(deserialized)

        val serialized = defaultMapper.writeValueAsString(deserialized)

        // with 2.9.3 prints
        //     {"name":"foo","query":"bar"}
        println(serialized)
    }

    class NonSerializable(private val field: Any?) {
        override fun toString() = "NonSerializable"
    }

    data class Foo(
        @JsonProperty("name") val name: String,
        @JsonIgnore val query: NonSerializable,
        @JsonProperty("query") val rawQuery: String
    ) {
        @JsonCreator
        constructor(
            @JsonProperty("name") name: String,
            @JsonProperty("query") rawQuery: String
        ) : this(name, NonSerializable(rawQuery), rawQuery)
    }
}
