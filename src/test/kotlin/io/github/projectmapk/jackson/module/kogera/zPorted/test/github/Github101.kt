package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.databind.InjectableValues
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID

class TestGithub101_JacksonInjectTest {
    @Test
    fun `JacksonInject-annotated parameters are populated when constructing Kotlin data classes`() {
        val contextualValue = UUID.randomUUID()
        val reader = jacksonObjectMapper()
            .readerFor(SomeDatum::class.java)
            .with(InjectableValues.Std(mapOf("context" to contextualValue)))
        assertEquals(
            SomeDatum("test", contextualValue),
            reader.readValue("""{ "value": "test" }""")
        )
    }

    data class SomeDatum(val value: String, @JacksonInject("context") val contextualValue: UUID)
}
