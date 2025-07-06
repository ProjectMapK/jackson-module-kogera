package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.databind.exc.InvalidNullException
import io.github.projectmapk.jackson.module.kogera.KotlinFeature
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GitHub976 {
    data class PrimitiveList(val list: List<Int>)

    @Test
    fun strictNullChecks() {
        val om = jacksonObjectMapper {
            enable(KotlinFeature.StrictNullChecks)
        }
        assertThrows<InvalidNullException> {
            om.readValue("""{"list": [""] }""", PrimitiveList::class.java)
        }
    }
}
