package io.github.projectmapk.jackson.module.kogera.zPorted

import com.fasterxml.jackson.core.json.JsonReadFeature
import com.fasterxml.jackson.core.json.JsonWriteFeature
import io.github.projectmapk.jackson.module.kogera.KotlinFeature.NullIsSameAsDefault
import io.github.projectmapk.jackson.module.kogera.KotlinFeature.NullToEmptyCollection
import io.github.projectmapk.jackson.module.kogera.KotlinFeature.NullToEmptyMap
import io.github.projectmapk.jackson.module.kogera.KotlinFeature.SingletonSupport
import io.github.projectmapk.jackson.module.kogera.KotlinFeature.StrictNullChecks
import io.github.projectmapk.jackson.module.kogera.KotlinModule
import io.github.projectmapk.jackson.module.kogera.jsonMapper
import io.github.projectmapk.jackson.module.kogera.kotlinModule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DslTest {

    @Test
    fun createModuleWithoutUsingInitializer() {
        val module = kotlinModule()
        assertNotNull(module)
    }

    @Test
    fun createModuleWithEmptyInitializer() {
        val module = kotlinModule {}
        assertNotNull(module)
    }

    @Test
    fun createModuleWithBuilderOptions() {
        val module = kotlinModule {
            withCacheSize(KotlinModule.CacheSize(123))
            enable(NullToEmptyCollection)
            enable(NullToEmptyMap)
            enable(NullIsSameAsDefault)
            enable(SingletonSupport)
            enable(StrictNullChecks)
        }

        assertNotNull(module)
        assertEquals(module.cacheSize, KotlinModule.CacheSize(123, 123))
        assertTrue(module.nullToEmptyCollection)
        assertTrue(module.nullToEmptyMap)
        assertTrue(module.nullIsSameAsDefault)
        assertEquals(module.singletonSupport, true)
        assertTrue(module.strictNullChecks)
    }

    @Test
    fun createJsonMapperWithoutUsingInitializer() {
        val mapper = jsonMapper()
        assertNotNull(mapper)
    }

    @Test
    fun createJsonMapperWithEmptyInitializer() {
        val mapper = jsonMapper {}
        assertNotNull(mapper)
    }

    @Test
    fun createJsonMapperWithBuilderOptions() {
        val mapper = jsonMapper {
            enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
            disable(JsonWriteFeature.QUOTE_FIELD_NAMES)
            configure(JsonReadFeature.ALLOW_SINGLE_QUOTES, true)

            addModule(
                kotlinModule {
                    enable(NullIsSameAsDefault)
                }
            )
        }

        assertNotNull(mapper)
        assertTrue(mapper.isEnabled(JsonReadFeature.ALLOW_JAVA_COMMENTS))
        assertFalse(mapper.isEnabled(JsonWriteFeature.QUOTE_FIELD_NAMES))
        assertTrue(mapper.isEnabled(JsonReadFeature.ALLOW_SINGLE_QUOTES))
        assertTrue(mapper.registeredModuleIds.any { it == "io.github.projectmapk.jackson.module.kogera.KotlinModule" })
    }
}
