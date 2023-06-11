package io.github.projectmapk.jackson.module.kogera._ported.test.github

import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Test

class TestGithub104 {
    abstract class SuperClass(val name: String)

    class SubClass(name: String) : SuperClass(name)
    // note this would fail if the constructor parameter is not named the same as the property

    @Test
    fun testIt() {
        val objectMapper = jacksonObjectMapper()

        val jsonValue = """{"name":"TestName"}"""

        objectMapper.readValue<SubClass>(jsonValue)
    }
}
