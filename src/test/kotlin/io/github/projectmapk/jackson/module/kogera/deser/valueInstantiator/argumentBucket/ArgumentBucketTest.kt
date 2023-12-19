package io.github.projectmapk.jackson.module.kogera.deser.valueInstantiator.argumentBucket

import io.github.projectmapk.jackson.module.kogera.ValueClassUnboxConverter
import io.github.projectmapk.jackson.module.kogera.deser.valueInstantiator.creator.ValueParameter
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private class ArgumentBucketTest {
    val mockConverters: List<ValueClassUnboxConverter<Any>?> = mockk {
        every { this@mockk[any()] } returns null
    }

    fun mockValueParameter(mockVararg: Boolean = false, mockOptional: Boolean = false) = mockk<ValueParameter> {
        every { isVararg } returns mockVararg
        every { isOptional } returns mockOptional
    }

    @Nested
    inner class BucketGeneratorTest {
        @Test
        fun basic() {
            val generator = BucketGenerator(
                (0..31).map { String::class.java },
                (0..31).map { mockValueParameter() },
                mockConverters
            )
            val result = generator.generate()

            assertEquals(32, result.valueParameterSize)
            assertTrue(result.arguments.all { it == null })
            assertEquals(1, result.masks.size)
            assertEquals(-1, result.masks[0])
        }

        @Test
        fun defaultPrimitives() {
            val generator = BucketGenerator(
                listOf(
                    Boolean::class.java,
                    Char::class.java,
                    Byte::class.java,
                    Short::class.java,
                    Int::class.java,
                    Float::class.java,
                    Long::class.java,
                    Double::class.java
                ),
                (1..8).map { mockValueParameter() },
                mockConverters
            )
            val result = generator.generate()

            assertEquals(false, result.arguments[0])
            assertEquals(0.toChar(), result.arguments[1])
            assertEquals(0.toByte(), result.arguments[2])
            assertEquals(0.toShort(), result.arguments[3])
            assertEquals(0, result.arguments[4])
            assertEquals(0f, result.arguments[5])
            assertEquals(0L, result.arguments[6])
            assertEquals(0.0, result.arguments[7])
        }

        @Test
        fun maskSize() {
            val generator = BucketGenerator(
                (0..32).map { String::class.java },
                (0..32).map { mockValueParameter() },
                mockConverters
            )
            val result = generator.generate()

            assertEquals(33, result.valueParameterSize)
            assertTrue(result.arguments.all { it == null })
            assertEquals(2, result.masks.size)
            assertEquals(-1, result.masks[0])
            assertEquals(1, result.masks[1])
        }

        // vararg is verified with an integration test.
    }

    @Test
    fun test() {
        val generator = BucketGenerator(
            (0..32).map { String::class.java },
            (0..32).map { mockValueParameter() },
            mockConverters
        )
        val sut = generator.generate()

        sut[0] = "0"
        sut[32] = "32"

        assertEquals(-2, sut.masks[0])
        assertEquals(0, sut.masks[1])
        assertFalse(sut.isFullInitialized)

        (1..31).forEach { sut[it] = it.toString() }

        assertEquals(0, sut.masks[0])
        assertEquals(0, sut.masks[1])
        (0..32).forEach { assertEquals(it.toString(), sut.arguments[it]) }
        assertTrue(sut.isFullInitialized)
    }

    @JvmInline
    value class V(val value: Int)

    @Test
    fun unboxTest() {
        @Suppress("UNCHECKED_CAST")
        val converter = ValueClassUnboxConverter(V::class.java) as ValueClassUnboxConverter<Any>
        val generator = BucketGenerator(
            listOf(Int::class.java, V::class.java),
            (1..2).map { mockValueParameter() },
            listOf(converter, null)
        )
        val bucket = generator.generate()

        bucket[0] = V(0)
        bucket[1] = 1

        assertEquals(0, bucket.arguments[0])
        assertEquals(1, bucket.arguments[1])
    }
}
