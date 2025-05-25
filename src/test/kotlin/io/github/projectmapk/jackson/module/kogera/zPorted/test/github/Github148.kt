package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import io.github.projectmapk.jackson.module.kogera.defaultMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TestGithub148 {
    enum class CorrectType {
        TYPEA,
        TYPEB
    }

    enum class IncorrectType {
        TYPEA {
            override fun desc() = "type a"
        },

        TYPEB {
            override fun desc() = "type b"
        };

        abstract fun desc(): String
    }

    data class CorrentBean(
        val name: String,
        val type: CorrectType
    )

    data class IncorrentBean(
        val name: String,
        val type: IncorrectType
    )

    @Nested
    inner class DemoApplicationTests {
        @Test
        fun correntBean() {
            assertEquals("{\"name\":\"corrent\",\"type\":\"TYPEA\"}", defaultMapper.writeValueAsString(CorrentBean("corrent", CorrectType.TYPEA)))
        }

        @Test
        fun incorrentBean() {
            assertEquals("{\"name\":\"incorrent\",\"type\":\"TYPEA\"}", defaultMapper.writeValueAsString(IncorrentBean("incorrent", IncorrectType.TYPEA)))
        }
    }
}
