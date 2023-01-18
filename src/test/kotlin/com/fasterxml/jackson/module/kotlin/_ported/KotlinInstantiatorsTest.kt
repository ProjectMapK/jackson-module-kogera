package com.fasterxml.jackson.module.kotlin._ported

import com.fasterxml.jackson.databind.deser.std.StdValueInstantiator
import com.fasterxml.jackson.module.kotlin.ReflectionCache
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.KotlinInstantiators
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.KotlinValueInstantiator
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class KotlinInstantiatorsTest {
    private val mapper = jacksonObjectMapper()
    private val deserConfig = mapper.deserializationConfig

    private val kotlinInstantiators = KotlinInstantiators(
        ReflectionCache(10),
        nullToEmptyCollection = false,
        nullToEmptyMap = false,
        nullIsSameAsDefault = false
    )

    @Test
    fun `Provides default instantiator for Java class`() {
        val javaType = mapper.constructType(String::class.java)
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

        val javaType = mapper.constructType(TestClass::class.java)
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
            mapper.constructType(DefaultClass::class.java)
        ) {}

        assertThrows<IllegalStateException> {
            kotlinInstantiators.findValueInstantiator(
                deserConfig,
                deserConfig.introspect(mapper.constructType(TestClass::class.java)),
                subClassInstantiator
            )
        }
    }
}
