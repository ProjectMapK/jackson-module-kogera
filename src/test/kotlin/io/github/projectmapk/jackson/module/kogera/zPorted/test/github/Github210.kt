package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Test

class TestGithub210 {
    class ExampleFail1(val stringItem: String, val regexItem: Regex)
    class ExampleFail2(val regexItem: Regex, val stringItem: String)

    class ExampleNoFail(val regexItem: RegexLike, val stringItem: String)
    class RegexLike(val pattern: String, val options: List<String>)

    @Test
    fun testSerDesOfRegex() {
        val happyJson = """{"stringItem":"hello","regexItem":{"options":[],"pattern":"test"}}"""
        val troubleJson = """{"regexItem":{"options":[],"pattern":"test"},"stringItem":"hello"}"""

        defaultMapper.readValue<ExampleNoFail>(happyJson)
        defaultMapper.readValue<ExampleNoFail>(troubleJson)

        defaultMapper.readValue<ExampleFail1>(happyJson)
        defaultMapper.readValue<ExampleFail2>(happyJson)

        // the following used to fail on stringItem being missing, the KotlinValueInstantiator is confused
        defaultMapper.readValue<ExampleFail1>(troubleJson) // fail {"regexItem":{"pattern":"test","options":[]},"stringItem":"hello"}
        defaultMapper.readValue<ExampleFail2>(troubleJson) // fail {"regexItem":{"pattern":"test","options":[]},"stringItem":"hello"}
    }
}
