package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import org.junit.jupiter.api.Test
import java.util.function.IntSupplier

class TestGithub167 {
    val samObject = IntSupplier { 42 }

    val answer = 42
    val samObjectSynthetic = IntSupplier { answer }

    @Test
    fun withKotlinExtension() {
        defaultMapper.writeValueAsString(samObject)
    }

    @Test
    fun withKotlinExtension_Synthetic() {
        defaultMapper.writeValueAsString(samObjectSynthetic)
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
