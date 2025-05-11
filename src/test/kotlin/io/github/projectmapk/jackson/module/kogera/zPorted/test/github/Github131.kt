package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestGithub131 {
    open class BaseClass(val name: String)

    class DerivedClass(name: String) : BaseClass(name)

    @Test
    fun testFailureCase() {
        val x = defaultMapper.readValue<DerivedClass>("""{"name":"abc"}""")
        assertEquals(DerivedClass("abc").name, x.name)
    }
}
