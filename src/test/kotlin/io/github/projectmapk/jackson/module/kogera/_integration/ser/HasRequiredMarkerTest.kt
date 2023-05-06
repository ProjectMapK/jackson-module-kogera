package io.github.projectmapk.jackson.module.kogera._integration.ser

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.projectmapk.jackson.module.kogera.KotlinFeature
import io.github.projectmapk.jackson.module.kogera.KotlinModule
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class HasRequiredMarkerTest {
    private inline fun <reified T> ObjectMapper.introspectSer(): BeanDescription =
        serializationConfig.introspect(serializationConfig.constructType(T::class.java))

    private fun BeanDescription.isRequired(propertyName: String): Boolean =
        this.findProperties().find { it.name == propertyName }?.isRequired ?: false

    val mapper = jacksonObjectMapper()

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
        val desc = mapper.introspectSer<GetterTarget>()

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
        val desc = mapper.introspectSer<AnnotationTarget>()

        assertTrue(desc.isRequired("nullableProp"))
        assertTrue(desc.isRequired("nullableField"))
    }

    class NullToDefaultTarget {
        @JvmField
        val collection: Collection<*> = emptyList<Any>()

        @JvmField
        val map: Map<*, *> = emptyMap<Any, Any>()
    }

    // @see KotlinPrimaryAnnotationIntrospector::AnnotatedField.hasRequiredMarker
    @Test
    fun failing() {
        val nullToDefaultMapper = ObjectMapper().registerModule(
            KotlinModule.Builder()
                .enable(KotlinFeature.NullToEmptyCollection)
                .enable(KotlinFeature.NullToEmptyMap)
                .build()
        )
        val desc = nullToDefaultMapper.introspectSer<NullToDefaultTarget>()

        assertFalse(
            desc.isRequired("collection"),
            "KotlinPrimaryAnnotationIntrospector::AnnotatedField.hasRequiredMarker fixed"
        )
        assertFalse(
            desc.isRequired("map"),
            "KotlinPrimaryAnnotationIntrospector::AnnotatedField.hasRequiredMarker fixed"
        )
    }
}
