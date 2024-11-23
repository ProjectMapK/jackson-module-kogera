package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import io.github.projectmapk.jackson.module.kogera.registerKotlinModule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "_type")
private sealed class BaseClass

private data class ChildClass(val text: String) : BaseClass()

class GitHub844 {
    @Test
    fun test() {
        val json = """
        {
            "_type": "ChildClass",
            "text": "Test"
        }
        """

        val jacksonObjectMapper = ObjectMapper().registerKotlinModule()
        val message = jacksonObjectMapper.readValue<BaseClass>(json)

        assertEquals(ChildClass("Test"), message)
    }
}
