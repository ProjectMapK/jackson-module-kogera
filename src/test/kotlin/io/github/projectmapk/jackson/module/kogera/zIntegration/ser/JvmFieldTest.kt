package io.github.projectmapk.jackson.module.kogera.zIntegration.ser

import io.github.projectmapk.jackson.module.kogera.defaultMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class JvmFieldTest {
    data class Src(
        @JvmField
        val `foo-foo`: String,
        @JvmField
        val `-bar`: String
    )

    @Test
    fun test() {
        val r = defaultMapper.writeValueAsString(Src("foo", "bar"))

        assertEquals("{\"foo-foo\":\"foo\",\"-bar\":\"bar\"}", r)
    }
}
