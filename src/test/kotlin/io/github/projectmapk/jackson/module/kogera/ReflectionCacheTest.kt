package io.github.projectmapk.jackson.module.kogera

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

private class ReflectionCacheTest {
    @Nested
    inner class JDKSerializabilityTest {
        @Test
        fun emptyCache() {
            val cache = ReflectionCache(100)
            val serialized = jdkSerialize(cache)

            assertDoesNotThrow {
                val deserialized = jdkDeserialize<ReflectionCache>(serialized)

                assertNotNull(deserialized)
                // Deserialized instance also do not raise exceptions
                deserialized.getJmClass(this::class.java)
            }
        }

        @Test
        fun notEmptyCache() {
            val cache = ReflectionCache(100).apply { getJmClass(this::class.java) }
            val serialized = jdkSerialize(cache)

            assertDoesNotThrow {
                val deserialized = jdkDeserialize<ReflectionCache>(serialized)

                assertNotNull(deserialized)
                // Deserialized instance also do not raise exceptions
                deserialized.getJmClass(this::class.java)
            }
        }
    }

    @JvmInline
    value class Value(val v: Int)
    data class ValueWrapper(val value: Value)

    @Nested
    inner class CachedValueTest {
        val reflectionCache = ReflectionCache(2)

        @Test
        fun getJmClassTest() {
            val v1 = reflectionCache.getJmClass(ReflectionCacheTest::class.java)
            val v2 = reflectionCache.getJmClass(ReflectionCacheTest::class.java)
            // The same instance is returned
            assertTrue(v1 === v2)

            reflectionCache.getJmClass(Value::class.java)
            val v3 = reflectionCache.getJmClass(ReflectionCacheTest::class.java)
            assertTrue(v1 === v3)

            reflectionCache.getJmClass(ValueWrapper::class.java)
            reflectionCache.getJmClass(Value::class.java) // for clear entry
            val v4 = reflectionCache.getJmClass(ReflectionCacheTest::class.java)
            assertTrue(v1 !== v4)
        }

        // findBoxedReturnType wasn't tested because it returns Class and therefore cannot instance comparison

        @Test
        fun getValueClassBoxConverterTest() {
            val v1 = reflectionCache.getValueClassBoxConverter(Int::class.java, Value::class.java)
            val v2 = reflectionCache.getValueClassBoxConverter(Int::class.java, Value::class.java)
            // The same instance is returned
            assertTrue(v1 === v2)

            reflectionCache.getJmClass(Value::class.java)
            val v3 = reflectionCache.getValueClassBoxConverter(Int::class.java, Value::class.java)
            assertTrue(v1 === v3)

            reflectionCache.getJmClass(ValueWrapper::class.java)
            reflectionCache.getJmClass(Value::class.java) // for clear entry
            val v4 = reflectionCache.getValueClassBoxConverter(Int::class.java, Value::class.java)
            assertTrue(v1 !== v4)
        }

        @Test
        fun getValueClassUnboxConverterTest() {
            val v1 = reflectionCache.getValueClassUnboxConverter(Value::class.java)
            val v2 = reflectionCache.getValueClassUnboxConverter(Value::class.java)
            // The same instance is returned
            assertTrue(v1 === v2)

            reflectionCache.getJmClass(Value::class.java)
            val v3 = reflectionCache.getValueClassUnboxConverter(Value::class.java)
            assertTrue(v1 === v3)

            reflectionCache.getJmClass(ValueWrapper::class.java)
            reflectionCache.getJmClass(Value::class.java) // for clear entry
            val v4 = reflectionCache.getValueClassUnboxConverter(Value::class.java)
            assertTrue(v1 !== v4)
        }
    }
}
