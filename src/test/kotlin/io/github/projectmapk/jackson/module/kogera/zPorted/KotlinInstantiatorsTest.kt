package io.github.projectmapk.jackson.module.kogera.zPorted

import com.fasterxml.jackson.databind.deser.std.StdValueInstantiator
import io.github.projectmapk.jackson.module.kogera.ReflectionCache
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.deser.valueInstantiator.KotlinInstantiators
import io.github.projectmapk.jackson.module.kogera.deser.valueInstantiator.KotlinValueInstantiator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class KotlinInstantiatorsTest {
    private val deserConfig = defaultMapper.deserializationConfig

    private val kotlinInstantiators = KotlinInstantiators(
        ReflectionCache(10, 10),
        nullToEmptyCollection = false,
        nullToEmptyMap = false,
        nullIsSameAsDefault = false
    )

    @Test
    fun `Provides default instantiator for Java class`() {
        val javaType = defaultMapper.constructType(String::class.java)
        val defaultInstantiator = StdValueInstantiator(deserConfig, javaType)
        val instantiator = kotlinInstantiators.findValueInstantiator(
            deserConfig,
            deserConfig.introspect(javaType),
            defaultInstantiator
        )

        assertEquals(defaultInstantiator, instantiator)
    }

    @Test
    fun `Provides KotlinValueInstantiator for Kotlin class`() {
        class TestClass

        val javaType = defaultMapper.constructType(TestClass::class.java)
        val instantiator = kotlinInstantiators.findValueInstantiator(
            deserConfig,
            deserConfig.introspect(javaType),
            StdValueInstantiator(deserConfig, javaType)
        )

        assertTrue(instantiator is StdValueInstantiator)
        assertTrue(instantiator::class == KotlinValueInstantiator::class)
    }

    @Test
    fun `Throws for Kotlin class when default instantiator isn't StdValueInstantiator`() {
        class TestClass
        class DefaultClass

        val subClassInstantiator = object : StdValueInstantiator(
            deserConfig,
            defaultMapper.constructType(DefaultClass::class.java)
        ) {}

        assertThrows<IllegalStateException> {
            kotlinInstantiators.findValueInstantiator(
                deserConfig,
                deserConfig.introspect(defaultMapper.constructType(TestClass::class.java)),
                subClassInstantiator
            )
        }
    }
}
