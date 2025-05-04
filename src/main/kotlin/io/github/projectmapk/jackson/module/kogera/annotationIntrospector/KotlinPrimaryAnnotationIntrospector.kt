package io.github.projectmapk.jackson.module.kogera.annotationIntrospector

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.introspect.Annotated
import com.fasterxml.jackson.databind.introspect.AnnotatedField
import com.fasterxml.jackson.databind.introspect.AnnotatedMember
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector
import com.fasterxml.jackson.databind.jsontype.NamedType
import io.github.projectmapk.jackson.module.kogera.JSON_PROPERTY_CLASS
import io.github.projectmapk.jackson.module.kogera.ReflectionCache
import io.github.projectmapk.jackson.module.kogera.jmClass.JmClass
import io.github.projectmapk.jackson.module.kogera.jmClass.JmProperty
import io.github.projectmapk.jackson.module.kogera.reconstructClass
import io.github.projectmapk.jackson.module.kogera.toSignature
import kotlinx.metadata.isNullable
import java.lang.reflect.Constructor
import java.lang.reflect.Method

// AnnotationIntrospector that overrides the behavior of the default AnnotationIntrospector
// (in most cases, JacksonAnnotationIntrospector).
// Original name: KotlinAnnotationIntrospector
internal class KotlinPrimaryAnnotationIntrospector(
    private val nullToEmptyCollection: Boolean,
    private val nullToEmptyMap: Boolean,
    private val cache: ReflectionCache
) : NopAnnotationIntrospector() {
    // If JsonProperty.required is true, the behavior is clearly specified and the result is paramount.
    // Otherwise, the required is determined from the configuration and the definition on Kotlin.
    override fun hasRequiredMarker(m: AnnotatedMember): Boolean? {
        return cache.getJmClass(m.member.declaringClass)?.let { jmClass ->
            // To avoid overwriting processing by other modules, annotations are checked after JmClass has been obtained
            _findAnnotation(m, JSON_PROPERTY_CLASS)
                ?.let { if (it.required) return true }

            when (m) {
                is AnnotatedField -> m.hasRequiredMarker(jmClass)
                is AnnotatedMethod -> m.getRequiredMarkerFromCorrespondingAccessor(jmClass)
                is AnnotatedParameter -> m.hasRequiredMarker(jmClass)
                else -> null
            }
        }
    }

    // Functions that call this may return incorrect results for value classes whose value type is Collection or Map,
    // but this is a rare case and difficult to handle, so it is not supported.
    private fun JavaType.hasDefaultEmptyValue() = (nullToEmptyCollection && isCollectionLikeType) ||
        (nullToEmptyMap && isMapLikeType)

    // The nullToEmpty option also affects serialization,
    // but deserialization is preferred because there is currently no way to distinguish between contexts.
    private fun AnnotatedField.hasRequiredMarker(jmClass: JmClass): Boolean? {
        // Direct access to `AnnotatedField` is only performed if there is no accessor (defined as JvmField),
        // so if an accessor is defined, it is ignored.
        return jmClass.findPropertyByField(annotated)
            // Since a property that does not currently have a getter cannot be defined,
            // only a check for the existence of a getter is performed.
            // https://youtrack.jetbrains.com/issue/KT-6519
            ?.let {
                if (it.getterName == null) !(it.returnType.isNullable || type.hasDefaultEmptyValue()) else null
            }
    }

    private fun JmProperty.isRequiredByNullability(): Boolean = !this.returnType.isNullable

    private fun AnnotatedMethod.getRequiredMarkerFromCorrespondingAccessor(
        jmClass: JmClass
    ): Boolean? = when (parameterCount) {
        0 -> jmClass.findPropertyByGetter(member)?.isRequiredByNullability()
        1 -> {
            if (this.getParameter(0).type.hasDefaultEmptyValue()) {
                false
            } else {
                val memberSignature = member.toSignature()
                jmClass.properties.find { it.setterSignature == memberSignature }?.isRequiredByNullability()
            }
        }
        else -> null
    }

    private fun AnnotatedParameter.hasRequiredMarker(jmClass: JmClass): Boolean? {
        val paramDef = when (val member = member) {
            is Constructor<*> -> jmClass.findJmConstructor(member)?.valueParameters
            is Method -> jmClass.companion?.findFunctionByMethod(member)?.valueParameters
            else -> null
        }?.let { it[index] } ?: return null // Return null if function on Kotlin cannot be determined

        // non required if...
        return when {
            // Argument definition is nullable
            paramDef.isNullable -> false
            // Default argument are defined
            paramDef.isOptional -> false
            // vararg is treated as an empty array because undefined input is allowed
            paramDef.isVararg -> false
            // The conversion in case of null is defined.
            type.hasDefaultEmptyValue() -> false
            else -> true
        }
    }

    /**
     * Subclasses can be detected automatically for sealed classes, since all possible subclasses are known
     * at compile-time to Kotlin. This makes [com.fasterxml.jackson.annotation.JsonSubTypes] redundant.
     */
    // The definition location was not changed from kotlin-module because
    // the result was the same whether it was defined in Primary or Fallback.
    override fun findSubtypes(a: Annotated): List<NamedType>? = cache.getJmClass(a.rawType)?.let { jmClass ->
        jmClass.sealedSubclasses.map { NamedType(it.reconstructClass()) }.ifEmpty { null }
    }
}
