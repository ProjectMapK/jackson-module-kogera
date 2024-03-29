package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Test

class TestGithub210 {
    class ExampleFail1(val stringItem: String, val regexItem: Regex)
    class ExampleFail2(val regexItem: Regex, val stringItem: String)

    class ExampleNoFail(val regexItem: RegexLike, val stringItem: String)
    class RegexLike(val pattern: String, val options: List<String>)

    val mapper = jacksonObjectMapper()

    @Test
    fun testSerDesOfRegex() {
        val happyJson = """{"stringItem":"hello","regexItem":{"options":[],"pattern":"test"}}"""
        val troubleJson = """{"regexItem":{"options":[],"pattern":"test"},"stringItem":"hello"}"""

        mapper.readValue<ExampleNoFail>(happyJson)
        mapper.readValue<ExampleNoFail>(troubleJson)

        mapper.readValue<ExampleFail1>(happyJson)
        mapper.readValue<ExampleFail2>(happyJson)

        // the following used to fail on stringItem being missing, the KotlinValueInstantiator is confused
        mapper.readValue<ExampleFail1>(troubleJson) // fail {"regexItem":{"pattern":"test","options":[]},"stringItem":"hello"}
        mapper.readValue<ExampleFail2>(troubleJson) // fail {"regexItem":{"pattern":"test","options":[]},"stringItem":"hello"}
    }
}
