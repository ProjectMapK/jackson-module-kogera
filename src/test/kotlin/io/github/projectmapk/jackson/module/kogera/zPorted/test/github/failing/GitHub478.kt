package io.github.projectmapk.jackson.module.kogera.zPorted.test.github.failing

import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT
import io.github.projectmapk.jackson.module.kogera.jsonMapper
import io.github.projectmapk.jackson.module.kogera.kotlinModule
import io.github.projectmapk.jackson.module.kogera.readValue
import io.github.projectmapk.jackson.module.kogera.zPorted.test.expectFailure
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GitHub478Test {
    val mapper = jsonMapper {
        addModule(kotlinModule())
        serializationInclusion(NON_DEFAULT)
    }

    data class Data(val flag: Boolean = true)

    @Test
    fun omitsDefaultValueWhenSerializing() {
        expectFailure<AssertionError>("GitHub478 has been fixed!") {
            assertEquals("""{}""", mapper.writeValueAsString(Data()))
        }
    }

    @Test
    fun serializesNonDefaultValue() {
        expectFailure<AssertionError>("GitHub478 has been fixed!") {
            assertEquals("""{"flag": false}""", mapper.writeValueAsString(Data(flag = false)))
        }
    }

    @Test
    fun usesDefaultWhenDeserializing() {
        assertEquals(Data(), mapper.readValue<Data>("{}"))
    }
}
