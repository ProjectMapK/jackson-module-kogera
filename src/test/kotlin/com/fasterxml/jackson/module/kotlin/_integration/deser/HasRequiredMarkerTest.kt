package com.fasterxml.jackson.module.kotlin._integration.deser

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class HasRequiredMarkerTest {
    private inline fun <reified T> ObjectMapper.introspectDeser(): BeanDescription =
        deserializationConfig.introspect(deserializationConfig.constructType(T::class.java))

    private fun BeanDescription.isRequired(propertyName: String): Boolean =
        this.findProperties().find { it.name == propertyName }?.isRequired ?: false

    val defaultMapper = jacksonObjectMapper()
    val nullToDefaultMapper = ObjectMapper().registerModule(
        KotlinModule.Builder()
            .enable(KotlinFeature.NullToEmptyCollection)
            .enable(KotlinFeature.NullToEmptyMap)
            .build()
    )

    data class ParamTarget(
        val nullable: String?,
        val hasDefault: String = "default",
        val collection: Collection<*>,
        val map: Map<*, *>,
        val nonNull: Any
    )

    @Nested
    inner class ParamTest {
        @Test
        fun defaultParam() {
            val desc = defaultMapper.introspectDeser<ParamTarget>()

            assertFalse(desc.isRequired("nullable"))
            assertFalse(desc.isRequired("hasDefault"))
            assertTrue(desc.isRequired("collection"))
            assertTrue(desc.isRequired("map"))
            assertTrue(desc.isRequired("nonNull"))
        }

        @Test
        fun nullToDefaultParam() {
            val desc = nullToDefaultMapper.introspectDeser<ParamTarget>()

            assertFalse(desc.isRequired("nullable"))
            assertFalse(desc.isRequired("hasDefault"))
            assertFalse(desc.isRequired("collection"))
            assertFalse(desc.isRequired("map"))
            assertTrue(desc.isRequired("nonNull"))
        }
    }

    class SetterTarget {
        var nullableProp: String? = null

        @JvmField
        var nullableField: String? = null

        lateinit var collectionProp: Collection<*>

        @JvmField
        var collectionField: Collection<*> = emptyList<Any>()

        lateinit var mapProp: Map<*, *>

        @JvmField
        var mapField: Map<*, *> = emptyMap<Any, Any>()

        lateinit var nonNullProp: Any

        @JvmField
        var nonNullField: Any = ""
    }

    @Nested
    inner class SetterTest {
        @Test
        fun defaultParam() {
            val desc = defaultMapper.introspectDeser<SetterTarget>()

            assertFalse(desc.isRequired("nullableProp"))
            assertFalse(desc.isRequired("nullableField"))
            assertTrue(desc.isRequired("collectionProp"))
            assertTrue(desc.isRequired("collectionField"))
            assertTrue(desc.isRequired("mapProp"))
            assertTrue(desc.isRequired("mapField"))
            assertTrue(desc.isRequired("nonNullProp"))
            assertTrue(desc.isRequired("nonNullField"))
        }

        @Test
        fun nullToDefaultParam() {
            val desc = nullToDefaultMapper.introspectDeser<SetterTarget>()

            assertFalse(desc.isRequired("nullableProp"))
            assertFalse(desc.isRequired("nullableField"))
            assertFalse(desc.isRequired("collectionProp"))
            assertFalse(desc.isRequired("collectionField"))
            assertFalse(desc.isRequired("mapProp"))
            assertFalse(desc.isRequired("mapField"))
            assertTrue(desc.isRequired("nonNullProp"))
            assertTrue(desc.isRequired("nonNullField"))
        }
    }

    data class AnnotationTarget(
        @param:JsonProperty(required = true)
        val nullableParam: String?,
        @param:JsonProperty(required = true)
        val hasDefaultParam: String = "default",
        @param:JsonProperty(required = true)
        val collectionParam: Collection<*>,
        @param:JsonProperty(required = true)
        val mapParam: Map<*, *>
    ) {
        @set:JsonProperty(required = true)
        var nullableProp: String? = null

        @field:JsonProperty(required = true)
        @JvmField
        var nullableField: String? = null

        @set:JsonProperty(required = true)
        lateinit var collectionProp: Collection<*>

        @field:JsonProperty(required = true)
        @JvmField
        var collectionField: Collection<*> = emptyList<Any>()

        @set:JsonProperty(required = true)
        lateinit var mapProp: Map<*, *>

        @field:JsonProperty(required = true)
        @JvmField
        var mapField: Map<*, *> = emptyMap<Any, Any>()
    }

    @Test
    fun overrideByAnnotationTest() {
        val desc = nullToDefaultMapper.introspectDeser<AnnotationTarget>()

        assertTrue(desc.isRequired("nullableParam"))
        assertTrue(desc.isRequired("hasDefaultParam"))
        assertTrue(desc.isRequired("collectionParam"))
        assertTrue(desc.isRequired("mapParam"))

        assertTrue(desc.isRequired("nullableProp"))
        assertTrue(desc.isRequired("nullableField"))
        assertTrue(desc.isRequired("collectionProp"))
        assertTrue(desc.isRequired("collectionField"))
        assertTrue(desc.isRequired("mapProp"))
        assertTrue(desc.isRequired("mapField"))
    }
}
