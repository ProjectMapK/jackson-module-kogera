package io.github.projectmapk.jackson.module.kogera.zIntegration.deser

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.databind.exc.InvalidNullException
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class JsonSetterTest {
    data class NullsSkip(@JsonSetter(nulls = Nulls.SKIP) val v: Int = -1)

    @Test
    fun nullsSkip() {
        val d = defaultMapper.readValue<NullsSkip>("""{"v":null}""")

        assertEquals(-1, d.v)
    }

    data class NullsFail(@JsonSetter(nulls = Nulls.FAIL) val v: Int?)

    @Nested
    inner class NullsFailTest {
        @Test
        fun onNull() {
            assertThrows<InvalidNullException> {
                defaultMapper.readValue<NullsFail>("""{"v":null}""")
            }
        }

        @Test
        fun onMissing() {
            assertThrows<InvalidNullException> {
                defaultMapper.readValue<NullsFail>("""{}""")
            }
        }
    }

    data class NullsAsEmpty(@JsonSetter(nulls = Nulls.AS_EMPTY) val v: Collection<*>)

    @Nested
    inner class NullsAsEmptyTest {
        @Test
        fun onNull() {
            val d = defaultMapper.readValue<NullsAsEmpty>("""{"v":null}""")
            assertTrue(d.v.isEmpty())
        }

        @Test
        fun onMissing() {
            val d = defaultMapper.readValue<NullsAsEmpty>("""{}""")
            assertTrue(d.v.isEmpty())
        }
    }
}
