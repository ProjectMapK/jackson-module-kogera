package io.github.projectmapk.jackson.module.kogera.zIntegration.ser.valueClass

import com.fasterxml.jackson.annotation.JsonKey
import io.github.projectmapk.jackson.module.kogera.jacksonMapperBuilder
import io.github.projectmapk.jackson.module.kogera.testPrettyWriter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class JsonKeyTest {
    @JvmInline
    value class JsonKeyGetter(val value: Int) {
        @get:JsonKey
        val jsonKey: String
            get() = this.toString()
    }

    interface IJsonKeyGetter {
        @get:JsonKey
        val jsonKey: String
            get() = this.toString()
    }

    @JvmInline
    value class JsonKeyGetterImplementation(val value: Int) : IJsonKeyGetter

    @JvmInline
    value class JsonKeyGetterImplementationDisabled(val value: Int) : IJsonKeyGetter {
        @get:JsonKey(false)
        override val jsonKey: String
            get() = super.jsonKey
    }

    private val writer = jacksonMapperBuilder().build().testPrettyWriter()

    @Test
    fun test() {
        val src = mapOf(
            JsonKeyGetter(0) to 0,
            JsonKeyGetterImplementation(1) to 1,
            JsonKeyGetterImplementationDisabled(2) to 2,
        )

        assertEquals(
            """
                {
                  "JsonKeyGetter(value=0)" : 0,
                  "JsonKeyGetterImplementation(value=1)" : 1,
                  "2" : 2
                }
            """.trimIndent(),
            writer.writeValueAsString(src),
        )
    }
}
