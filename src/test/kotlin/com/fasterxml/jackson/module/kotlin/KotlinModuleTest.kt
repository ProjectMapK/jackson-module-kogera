package com.fasterxml.jackson.module.kotlin

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class KotlinModuleTest {
    @Test
    fun jdkSerializabilityTest() {
        val module = KotlinModule.Builder().apply {
            withReflectionCacheSize(123)
            enable(KotlinFeature.NullToEmptyCollection)
            enable(KotlinFeature.NullToEmptyMap)
            enable(KotlinFeature.NullIsSameAsDefault)
            enable(KotlinFeature.SingletonSupport)
            enable(KotlinFeature.StrictNullChecks)
        }.build()

        val serialized = jdkSerialize(module)

        assertDoesNotThrow {
            val deserialized = jdkDeserialize<KotlinModule>(serialized)

            assertNotNull(deserialized)
            assertEquals(123, deserialized.reflectionCacheSize)
            assertTrue(deserialized.nullToEmptyCollection)
            assertTrue(deserialized.nullToEmptyMap)
            assertTrue(deserialized.nullIsSameAsDefault)
            assertTrue(deserialized.singletonSupport)
            assertTrue(deserialized.strictNullChecks)
        }
    }
}
