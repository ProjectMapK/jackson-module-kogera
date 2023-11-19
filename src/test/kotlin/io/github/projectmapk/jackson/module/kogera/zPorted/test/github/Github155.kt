package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Test

class TestGithub155 {
    data class Foo @JvmOverloads constructor(
        @JsonProperty("name") val name: String,
        @JsonProperty("age") val age: Int = 0,
        @JsonProperty("country") val country: String = "whatever",
        @JsonProperty("city") val city: String = "nada"
    )

    @Test
    fun testGithub155() {
        jacksonObjectMapper().readValue<Foo>(
            """
            {"name":"fred","age":12,"country":"Libertad","city":"Northville"}
            """.trimIndent()
        )
    }
}
