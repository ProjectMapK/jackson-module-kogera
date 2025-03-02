package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.databind.MapperFeature
import io.github.projectmapk.jackson.module.kogera.jsonMapper
import io.github.projectmapk.jackson.module.kogera.kotlinModule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GitHub314 {
    // Since Nothing? is compiled as a Void, it can be serialized by specifying ALLOW_VOID_VALUED_PROPERTIES
    data object NothingData {
        val data: Nothing? = null
    }

    @Test
    fun test() {
        val expected = """{"data":null}"""

        val withoutKotlinModule = jsonMapper { enable(MapperFeature.ALLOW_VOID_VALUED_PROPERTIES) }
        assertEquals(expected, withoutKotlinModule.writeValueAsString(NothingData))

        val withKotlinModule = jsonMapper {
            enable(MapperFeature.ALLOW_VOID_VALUED_PROPERTIES)
            addModule(kotlinModule())
        }

        assertEquals(expected, withKotlinModule.writeValueAsString(NothingData))
    }
}
