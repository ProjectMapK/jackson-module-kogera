package io.github.projectmapk.jackson.module.kogera

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

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

    @Nested
    inner class SetCacheSizesTest {
        @Test
        fun `Cannot set initialCacheSize to a value larger than maxCacheSize`() {
            assertThrows<IllegalArgumentException> {
                KotlinModule.Builder().apply {
                    withInitialCacheSize(maxCacheSize + 1)
                }
            }
        }

        @Test
        fun `Cannot set maxCacheSize to a value smaller than initialCacheSize`() {
            assertThrows<IllegalArgumentException> {
                KotlinModule.Builder().apply {
                    withMaxCacheSize(initialCacheSize - 1)
                }
            }
        }

        @Test
        fun `Cannot set maxCacheSize to a value smaller than 15`() {
            assertThrows<IllegalArgumentException> {
                KotlinModule.Builder().apply {
                    withInitialCacheSize(0)
                    withMaxCacheSize(15)
                }
            }
        }

        @Test
        fun test() {
            assertDoesNotThrow {
                KotlinModule.Builder().apply {
                    withInitialCacheSize(0)
                    withMaxCacheSize(16)
                }
            }
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun jdkSerializabilityTest(enabled: Boolean) {
        val module = KotlinModule.Builder().apply {
            withInitialCacheSize(123)
            withMaxCacheSize(321)

            KotlinFeature.values().forEach {
                configure(it, enabled)
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
