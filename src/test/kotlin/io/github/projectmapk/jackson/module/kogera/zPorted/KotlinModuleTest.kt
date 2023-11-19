package io.github.projectmapk.jackson.module.kogera.zPorted

import io.github.projectmapk.jackson.module.kogera.KotlinFeature.NullIsSameAsDefault
import io.github.projectmapk.jackson.module.kogera.KotlinFeature.NullToEmptyCollection
import io.github.projectmapk.jackson.module.kogera.KotlinFeature.NullToEmptyMap
import io.github.projectmapk.jackson.module.kogera.KotlinFeature.SingletonSupport
import io.github.projectmapk.jackson.module.kogera.KotlinFeature.StrictNullChecks
import io.github.projectmapk.jackson.module.kogera.KotlinModule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class KotlinModuleTest {
    /**
     * Ensure that the default Builder matches Feature default settings.
     */
    @Test
    fun builderDefaultsMatchFeatures() {
        val module = KotlinModule.Builder().build()

        assertEquals(KotlinModule.CacheSize(512), module.cacheSize)
        assertFalse(module.nullToEmptyCollection)
        assertFalse(module.nullToEmptyMap)
        assertFalse(module.nullIsSameAsDefault)
        assertEquals(module.singletonSupport, false)
        assertFalse(module.strictNullChecks)
    }

    @Test
    fun builder_Defaults() {
        val module = KotlinModule.Builder().build()

        assertEquals(KotlinModule.CacheSize(512), module.cacheSize)
        assertFalse(module.nullToEmptyCollection)
        assertFalse(module.nullToEmptyMap)
        assertFalse(module.nullIsSameAsDefault)
        assertEquals(false, module.singletonSupport)
        assertFalse(module.strictNullChecks)
    }

    @Test
    fun builder_SetAll() {
        val module = KotlinModule.Builder().apply {
            withCacheSize(KotlinModule.CacheSize(initialCacheSize = 123))
            enable(NullToEmptyCollection)
            enable(NullToEmptyMap)
            enable(NullIsSameAsDefault)
            enable(SingletonSupport)
            enable(StrictNullChecks)
        }.build()

        assertEquals(123, module.cacheSize.initialCacheSize)
        assertTrue(module.nullToEmptyCollection)
        assertTrue(module.nullToEmptyMap)
        assertTrue(module.nullIsSameAsDefault)
        assertEquals(true, module.singletonSupport)
        assertTrue(module.strictNullChecks)
    }

    @Test
    fun builder_NullToEmptyCollection() {
        val module = KotlinModule.Builder().apply {
            enable(NullToEmptyCollection)
        }.build()

        assertTrue(module.nullToEmptyCollection)
    }

    @Test
    fun builder_NullToEmptyMap() {
        val module = KotlinModule.Builder().apply {
            enable(NullToEmptyMap)
        }.build()

        assertTrue(module.nullToEmptyMap)
    }

    @Test
    fun builder_NullIsSameAsDefault() {
        val module = KotlinModule.Builder().apply {
            enable(NullIsSameAsDefault)
        }.build()

        assertTrue(module.nullIsSameAsDefault)
    }

    @Test
    fun builder_EnableCanonicalSingletonSupport() {
        val module = KotlinModule.Builder().apply {
            enable(SingletonSupport)
        }.build()

        assertEquals(true, module.singletonSupport)
    }

    @Test
    fun builder_EnableStrictNullChecks() {
        val module = KotlinModule.Builder().apply {
            enable(StrictNullChecks)
        }.build()

        assertTrue(module.strictNullChecks)
    }
}
