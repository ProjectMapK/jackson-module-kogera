package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class TestGithub308 {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class TestDto(
        @JsonIgnore
        var id: Long? = null,
        var cityId: Int? = null
    ) {
        private var unpackId: Int?
            get() = cityId // Why define get: https://youtrack.jetbrains.com/issue/KT-6519

            @JsonProperty("id")
            set(value) { cityId = value }
    }

    @Test
    fun createTestDto() {
        val dto: TestDto = jacksonObjectMapper().readValue("""{"id":12345}""")

        assertNotNull(dto)
        assertNull(dto.id)
        assertEquals(dto.cityId, 12345)
    }
}
