package io.github.projectmapk.jackson.module.kogera._ported.test.github

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Github526 {
    data class D(@JsonSetter(nulls = Nulls.SKIP) val v: Int = -1)

    @Test
    fun test() {
        val mapper = jacksonObjectMapper()
        val d = mapper.readValue<D>("""{"v":null}""")

        assertEquals(-1, d.v)
    }
}
