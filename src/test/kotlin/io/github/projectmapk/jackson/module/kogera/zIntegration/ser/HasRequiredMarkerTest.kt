package io.github.projectmapk.jackson.module.kogera.zIntegration.ser

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.projectmapk.jackson.module.kogera.KotlinFeature
import io.github.projectmapk.jackson.module.kogera.KotlinModule
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class HasRequiredMarkerTest {
    private inline fun <reified T> ObjectMapper.introspectSer(): BeanDescription = serializationConfig
        .introspect(serializationConfig.constructType(T::class.java))

    private fun BeanDescription.isRequired(propertyName: String): Boolean = findProperties()
        .find { it.name == propertyName }
        ?.isRequired == true

    class GetterTarget {
        val nullableProp: String? get() = null

        @JvmField
        val nullableField: String? = null

        val nonNullProp: Any get() = ""

        @JvmField
        var nonNullField: Any = ""
    }

    @Test
    fun getterTest() {
        val desc = defaultMapper.introspectSer<GetterTarget>()

        assertFalse(desc.isRequired("nullableProp"))
        assertFalse(desc.isRequired("nullableField"))
        assertTrue(desc.isRequired("nonNullProp"))
        assertTrue(desc.isRequired("nonNullField"))
    }

    class AnnotationTarget {
        @get:JsonProperty(required = true)
        val nullableProp: String? get() = null

        @field:JsonProperty(required = true)
        @JvmField
        val nullableField: String? = null
    }

    @Test
    fun overrideByAnnotationTest() {
        val desc = defaultMapper.introspectSer<AnnotationTarget>()

        assertTrue(desc.isRequired("nullableProp"))
        assertTrue(desc.isRequired("nullableField"))
    }

    class NullToDefaultTarget {
        @JvmField
        val collection: Collection<*> = emptyList<Any>()

        @JvmField
        val map: Map<*, *> = emptyMap<Any, Any>()
    }

    @Test
    fun `nullToEmpty does not affect for field`() {
        val nullToDefaultMapper = ObjectMapper().registerModule(
            KotlinModule.Builder()
                .enable(KotlinFeature.NullToEmptyCollection)
                .enable(KotlinFeature.NullToEmptyMap)
                .build(),
        )
        val desc = nullToDefaultMapper.introspectSer<NullToDefaultTarget>()

        assertTrue(desc.isRequired("collection"))
        assertTrue(desc.isRequired("map"))
    }
}
