package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Github630 {
    data class Dto(
        // from #570, #603
        val FOO: Int = 0,
        val bAr: Int = 0,
        @JsonProperty("b")
        val BAZ: Int = 0,
        @JsonProperty("q")
        val qUx: Int = 0,
        // from #71
        internal val quux: Int = 0,
        // from #434
        val `corge-corge`: Int = 0,
        // additional
        @get:JvmName("aaa")
        val grault: Int = 0
    )

    @Test
    fun test() {
        val dto = Dto()

        assertEquals(
            """{"FOO":0,"bAr":0,"b":0,"q":0,"quux":0,"corge-corge":0,"grault":0}""",
            defaultMapper.writeValueAsString(dto)
        )
    }
}
