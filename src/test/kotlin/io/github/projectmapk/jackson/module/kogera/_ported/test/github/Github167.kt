package io.github.projectmapk.jackson.module.kogera._ported.test.github

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import org.junit.jupiter.api.Test
import java.util.function.IntSupplier

class TestGithub167 {
    val samObject = IntSupplier { 42 }

    val answer = 42
    val samObjectSynthetic = IntSupplier { answer }

    @Test
    fun withKotlinExtension() {
        jacksonObjectMapper().writeValueAsString(samObject)
    }

    @Test
    fun withKotlinExtension_Synthetic() {
        jacksonObjectMapper().writeValueAsString(samObjectSynthetic)
    }

    @Test
    fun withoutKotlinExtension() {
        ObjectMapper().writeValueAsString(samObject)
    }

    @Test
    fun withoutKotlinExtension_Synthetic() {
        ObjectMapper().writeValueAsString(samObjectSynthetic)
    }
}
