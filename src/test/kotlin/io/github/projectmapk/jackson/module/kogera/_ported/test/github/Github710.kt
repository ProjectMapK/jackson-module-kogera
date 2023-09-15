package io.github.projectmapk.jackson.module.kogera._ported.test.github

import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Github710 {
    interface I<T> {
        val foo: T
        val bAr: T get() = foo
    }

    class C(override val foo: Int) : I<Int>

    @Test
    fun test() {
        val mapper = jacksonObjectMapper()
        val result = mapper.writeValueAsString(C(1))

        assertEquals("""{"foo":1,"bAr":1}""", result)
    }
}
