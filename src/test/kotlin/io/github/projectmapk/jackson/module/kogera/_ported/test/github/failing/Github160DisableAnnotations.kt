package io.github.projectmapk.jackson.module.kogera._ported.test.github.failing

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import io.github.projectmapk.jackson.module.kogera._ported.test.expectFailure
import io.github.projectmapk.jackson.module.kogera.jacksonMapperBuilder
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Test

class TestGithub160 {
    data class DataClass(val blah: String)

    @Test
    fun dataClass() {
        val mapper = jacksonMapperBuilder()
            .configure(MapperFeature.USE_ANNOTATIONS, false)
            .build()
        expectFailure<MismatchedInputException>("GitHub #160 has been fixed!") {
            mapper.readValue<DataClass>("""{"blah":"blah"}""")
        }
    }
}
