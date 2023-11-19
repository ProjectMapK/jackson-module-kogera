package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestGithub158 {
    enum class SampleImpl constructor(override val value: String) : Sample {
        One("oney"),
        Two("twoey")
    }

    interface Sample {
        val value: String
    }

    data class SampleContainer(@JsonDeserialize(`as` = SampleImpl::class) val sample: Sample)

    @Test
    fun testEnumSerDeser() {
        val mapper = jacksonObjectMapper()

        val original = SampleContainer(SampleImpl.One)

        val json = mapper.writeValueAsString(original)
//        println(json)
        val obj = mapper.readValue<SampleContainer>(json)
        assertEquals(original, obj)
    }
}
