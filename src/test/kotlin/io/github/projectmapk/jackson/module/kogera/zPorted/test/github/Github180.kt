package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class TestGithub180 {
    class TestClass(val instantName: String? = null, val someInt: Int? = null) {
        companion object {
            @JvmStatic
            @JsonCreator
            fun create(
                @JsonProperty("instantName") instantName: String?,
                @JsonProperty("someInt") someInt: Int?
            ): TestClass {
                return TestClass(instantName, someInt)
            }
        }
    }

    @Test
    fun testMissingProperty() {
        val obj = defaultMapper.readValue<TestClass>("""{}""")
        assertNull(obj.instantName)
        assertNull(obj.someInt)
    }
}
