package io.github.projectmapk.jackson.module.kogera

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class KotlinModuleTest {
    @Test
    fun setKotlinFeatureTest() {
        val builder = KotlinModule.Builder().apply {
            KotlinFeature.values().forEach { enable(it) }
        }

        assertTrue(builder.isEnabled(KotlinFeature.NullToEmptyCollection))
        assertTrue(builder.isEnabled(KotlinFeature.NullToEmptyMap))
        assertTrue(builder.isEnabled(KotlinFeature.NullIsSameAsDefault))
        assertTrue(builder.isEnabled(KotlinFeature.SingletonSupport))
        assertTrue(builder.isEnabled(KotlinFeature.StrictNullChecks))
        assertTrue(builder.isEnabled(KotlinFeature.CopySyntheticConstructorParameterAnnotations))
        assertTrue(builder.isEnabled(KotlinFeature.UseJavaDurationConversion))

        builder.apply {
            KotlinFeature.values().forEach { disable(it) }
        }

        assertFalse(builder.isEnabled(KotlinFeature.NullToEmptyCollection))
        assertFalse(builder.isEnabled(KotlinFeature.NullToEmptyMap))
        assertFalse(builder.isEnabled(KotlinFeature.NullIsSameAsDefault))
        assertFalse(builder.isEnabled(KotlinFeature.SingletonSupport))
        assertFalse(builder.isEnabled(KotlinFeature.StrictNullChecks))
        assertFalse(builder.isEnabled(KotlinFeature.CopySyntheticConstructorParameterAnnotations))
        assertFalse(builder.isEnabled(KotlinFeature.UseJavaDurationConversion))
    }

    @Test
    fun jdkSerializabilityTest() {
        val module = KotlinModule.Builder().apply {
            withInitialCacheSize(123)
            withMaxCacheSize(321)

            KotlinFeature.values().forEach {
                enable(it)
            }
        }.build()

        val serialized = jdkSerialize(module)

        assertDoesNotThrow {
            val deserialized = jdkDeserialize<KotlinModule>(serialized)

            assertNotNull(deserialized)
            assertEquals(123, deserialized.initialCacheSize)
            assertEquals(321, deserialized.maxCacheSize)
            assertEquals(module.nullToEmptyCollection, deserialized.nullToEmptyCollection)
            assertEquals(module.nullToEmptyMap, deserialized.nullToEmptyMap)
            assertEquals(module.nullIsSameAsDefault, deserialized.nullIsSameAsDefault)
            assertEquals(module.singletonSupport, deserialized.singletonSupport)
            assertEquals(module.strictNullChecks, deserialized.strictNullChecks)
            assertEquals(module.copySyntheticConstructorParameterAnnotations, deserialized.copySyntheticConstructorParameterAnnotations)
            assertEquals(module.useJavaDurationConversion, deserialized.useJavaDurationConversion)
        }
    }
}
