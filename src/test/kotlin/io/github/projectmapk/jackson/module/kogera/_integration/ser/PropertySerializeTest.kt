package io.github.projectmapk.jackson.module.kogera._integration.ser

import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PropertySerializeTest {
    data class Src(
        // Serialized by property name in Kotlin even when not following getter naming conventions
        @get:JvmName("renamed") val fooFoo: Int,
        // https://github.com/FasterXML/jackson-module-kotlin/issues/600
        val isBar: Boolean,
        val bar: String,
        // https://github.com/FasterXML/jackson-module-kotlin/pull/451
        @Suppress("PropertyName") val `baz-baz`: String,
        // https://github.com/FasterXML/jackson-module-kotlin/issues/503
        val nQux: Int
    )

    @Test
    fun test() {
        assertEquals(
            """{"fooFoo":0,"isBar":true,"bar":"bar","baz-baz":"baz-baz","nQux":1}""",
            jacksonObjectMapper()
                .writeValueAsString(Src(0, true, "bar", "baz-baz", 1))
        )
    }
}
