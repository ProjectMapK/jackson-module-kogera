package io.github.projectmapk.jackson.module.kogera.deser.valueInstantiator.argumentBucket

import io.github.projectmapk.jackson.module.kogera.ValueClassUnboxConverter
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

    @Nested
    inner class BucketGeneratorTest {
        @Test
        fun basic() {
            val generator = BucketGenerator((0..31).map { String::class.java }, false, mockConverters)
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
                false,
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
            val generator = BucketGenerator((0..32).map { String::class.java }, false, mockConverters)
            val result = generator.generate()

            assertEquals(33, result.valueParameterSize)
            assertTrue(result.arguments.all { it == null })
            assertEquals(2, result.masks.size)
            assertEquals(-1, result.masks[0])
            assertEquals(-1, result.masks[1])
        }

        @Test
        fun hasVararg() {
            val primitiveGenerator = BucketGenerator(listOf(IntArray::class.java), true, mockConverters)
            val primitiveResult = primitiveGenerator.generate()

            assertEquals(1, primitiveResult.valueParameterSize)
            assertArrayEquals(intArrayOf(), primitiveResult.arguments[0] as IntArray)
            assertEquals(1, primitiveResult.masks.size)
            assertEquals(-1, primitiveResult.masks[0])

            val objectGenerator = BucketGenerator(listOf(Array<Int>::class.java), true, mockConverters)
            val objectResult = objectGenerator.generate()
            assertEquals(1, objectResult.valueParameterSize)
            @Suppress("UNCHECKED_CAST")
            assertArrayEquals(emptyArray<Int>(), objectResult.arguments[0] as Array<Int>)
            assertEquals(1, objectResult.masks.size)
            assertEquals(-1, objectResult.masks[0])
        }
    }

    @Test
    fun test() {
        val generator = BucketGenerator((0..32).map { String::class.java }, false, mockConverters)
        val sut = generator.generate()

        sut[0] = "0"
        sut[32] = "32"

        assertEquals(-2, sut.masks[0])
        assertEquals(-2, sut.masks[1])
        assertFalse(sut.isFullInitialized)

        (1..31).forEach { sut[it] = it.toString() }

        assertEquals(0, sut.masks[0])
        assertEquals(-2, sut.masks[1])
        (0..32).forEach { assertEquals(it.toString(), sut.arguments[it]) }
        assertTrue(sut.isFullInitialized)
    }
}
