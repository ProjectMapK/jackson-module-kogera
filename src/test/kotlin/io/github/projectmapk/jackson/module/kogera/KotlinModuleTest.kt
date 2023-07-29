package io.github.projectmapk.jackson.module.kogera

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

            KotlinFeature.values().forEach {
                enable(it)
            }
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
            assertTrue(deserialized.copySyntheticConstructorParameterAnnotations)
        }
    }
}
