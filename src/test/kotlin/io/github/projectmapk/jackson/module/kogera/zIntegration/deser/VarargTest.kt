package io.github.projectmapk.jackson.module.kogera.zIntegration.deser

import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class VarargTest {
    val mapper = jacksonObjectMapper()

    class OnlyVararg(vararg val v: Int)

    @Nested
    inner class OnlyVarargTest {
        @Test
        fun hasArgs() {
            val r = mapper.readValue<OnlyVararg>("""{"v":[1,2,3]}""")
            assertEquals(listOf(1, 2, 3), r.v.asList())
        }

        @Test
        fun empty() {
            val r = mapper.readValue<OnlyVararg>("""{"v":[]}""")
            assertTrue(r.v.isEmpty())
        }

        @Test
        fun undefined() {
            val r = mapper.readValue<OnlyVararg>("""{}""")
            assertTrue(r.v.isEmpty())
        }
    }

    class HeadVararg(vararg val v: Int?, val i: Int)

    @Nested
    inner class HeadVarargTest {
        @Test
        fun hasArgs() {
            val r = mapper.readValue<HeadVararg>("""{"i":0,"v":[1,2,null]}""")
            assertEquals(listOf(1, 2, null), r.v.asList())
            assertEquals(0, r.i)
        }

        @Test
        fun empty() {
            val r = mapper.readValue<HeadVararg>("""{"i":0,"v":[]}""")
            assertTrue(r.v.isEmpty())
            assertEquals(0, r.i)
        }

        @Test
        fun undefined() {
            val r = mapper.readValue<HeadVararg>("""{"i":0}""")
            assertTrue(r.v.isEmpty())
            assertEquals(0, r.i)
        }
    }

    class TailVararg(val i: Int, vararg val v: String)

    @Nested
    inner class TailVarargTest {
        @Test
        fun hasArgs() {
            val r = mapper.readValue<TailVararg>("""{"i":0,"v":["foo","bar","baz"]}""")
            assertEquals(listOf("foo", "bar", "baz"), r.v.asList())
            assertEquals(0, r.i)
        }

        @Test
        fun empty() {
            val r = mapper.readValue<TailVararg>("""{"i":0,"v":[]}""")
            assertTrue(r.v.isEmpty())
            assertEquals(0, r.i)
        }

        @Test
        fun undefined() {
            val r = mapper.readValue<TailVararg>("""{"i":0}""")
            assertTrue(r.v.isEmpty())
            assertEquals(0, r.i)
        }
    }

    class HasDefaultVararg(vararg val v: String? = arrayOf("foo", "bar"))

    @Nested
    inner class HasDefaultVarargTest {
        @Test
        fun hasArgs() {
            val r = mapper.readValue<HasDefaultVararg>("""{"v":["foo","bar",null]}""")
            assertEquals(listOf("foo", "bar", null), r.v.asList())
        }

        @Test
        fun empty() {
            val r = mapper.readValue<HasDefaultVararg>("""{"v":[]}""")
            assertTrue(r.v.isEmpty())
        }

        @Test
        fun undefined() {
            val r = mapper.readValue<HasDefaultVararg>("""{}""")
            assertEquals(listOf("foo", "bar"), r.v.asList())
        }
    }
}
