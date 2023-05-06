package io.github.projectmapk.jackson.module.kogera

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class ReflectionCacheTest {
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
                deserialized.getKmClass(this::class.java)
            }
        }

        @Test
        fun notEmptyCache() {
            val cache = ReflectionCache(100).apply { getKmClass(this::class.java) }
            val serialized = jdkSerialize(cache)

            assertDoesNotThrow {
                val deserialized = jdkDeserialize<ReflectionCache>(serialized)

                assertNotNull(deserialized)
                // Deserialized instance also do not raise exceptions
                deserialized.getKmClass(this::class.java)
            }
        }
    }
}
