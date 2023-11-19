package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.databind.MapperFeature
import io.github.projectmapk.jackson.module.kogera.jacksonMapperBuilder
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestGithub47 {

    class ConfigItem(val configItemId: String)

    @Test
    fun testCaseInsensitivePropertyNames() {
        val mapper = jacksonMapperBuilder()
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .build()

        val jsonWithMismtachedPropertyName = """
                    {
                        "configItemID": "test"
                    }
                   """

        val item: ConfigItem = mapper.readValue(jsonWithMismtachedPropertyName)
        assertEquals("test", item.configItemId)
    }
}
