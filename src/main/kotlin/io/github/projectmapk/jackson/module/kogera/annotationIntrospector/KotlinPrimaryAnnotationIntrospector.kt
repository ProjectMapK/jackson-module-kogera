package io.github.projectmapk.jackson.module.kogera.annotationIntrospector

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.cfg.MapperConfig
import com.fasterxml.jackson.databind.introspect.Annotated
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor
import com.fasterxml.jackson.databind.introspect.AnnotatedField
import com.fasterxml.jackson.databind.introspect.AnnotatedMember
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector
import com.fasterxml.jackson.databind.jsontype.NamedType
import io.github.projectmapk.jackson.module.kogera.JmClass
import io.github.projectmapk.jackson.module.kogera.ReflectionCache
import io.github.projectmapk.jackson.module.kogera.hasCreatorAnnotation
import io.github.projectmapk.jackson.module.kogera.reconstructClass
import io.github.projectmapk.jackson.module.kogera.toSignature
import kotlinx.metadata.KmClassifier
import kotlinx.metadata.KmProperty
import kotlinx.metadata.KmValueParameter
import kotlinx.metadata.declaresDefaultValue
import kotlinx.metadata.isNullable
import kotlinx.metadata.isSecondary
import kotlinx.metadata.jvm.getterSignature
import kotlinx.metadata.jvm.setterSignature
import kotlinx.metadata.jvm.signature
import java.lang.reflect.Constructor
import java.lang.reflect.Executable
import java.lang.reflect.Method
import java.lang.reflect.Modifier

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
        val byAnnotation = _findAnnotation(m, JsonProperty::class.java)
            ?.let { if (it.required) return true else false }

        return cache.getJmClass(m.member.declaringClass)?.let {
            when (m) {
                is AnnotatedField -> m.hasRequiredMarker(it)
                is AnnotatedMethod -> m.getRequiredMarkerFromCorrespondingAccessor(it)
                is AnnotatedParameter -> m.hasRequiredMarker(it)
                else -> null
            }
        } ?: byAnnotation // If a JsonProperty is available, use it to reduce processing costs.
    }

    // Functions that call this may return incorrect results for value classes whose value type is Collection or Map,
    // but this is a rare case and difficult to handle, so it is not supported.
    private fun JavaType.hasDefaultEmptyValue() =
        (nullToEmptyCollection && isCollectionLikeType) || (nullToEmptyMap && isMapLikeType)

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
                if (it.getterSignature == null) !(it.returnType.isNullable || type.hasDefaultEmptyValue()) else null
            }
    }

    private fun KmProperty.isRequiredByNullability(): Boolean = !this.returnType.isNullable

    private fun AnnotatedMethod.getRequiredMarkerFromCorrespondingAccessor(jmClass: JmClass): Boolean? =
        when (parameterCount) {
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
            is Constructor<*> -> jmClass.findKmConstructor(member)?.valueParameters
            is Method -> jmClass.companion?.findFunctionByMethod(member)?.valueParameters
            else -> null
        }?.let { it[index] } ?: return null // Return null if function on Kotlin cannot be determined

        // non required if...
        return when {
            // Argument definition is nullable
            paramDef.type.isNullable -> false
            // Default argument are defined
            paramDef.declaresDefaultValue -> false
            // vararg is treated as an empty array because undefined input is allowed
            paramDef.varargElementType != null -> false
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

    // Return Mode.DEFAULT if ann is a Primary Constructor and the condition is satisfied.
    // Currently, there is no way to define the priority of a Creator,
    // so the presence or absence of a JsonCreator is included in the decision.
    // The reason for overriding the JacksonAnnotationIntrospector is to reduce overhead.
    // In rare cases, a problem may occur,
    // but it is assumed that the problem can be solved by adjusting the order of module registration.
    override fun findCreatorAnnotation(config: MapperConfig<*>, ann: Annotated): JsonCreator.Mode? {
        (ann as? AnnotatedConstructor)?.takeIf { 0 < it.parameterCount } ?: return null

        val declaringClass = ann.declaringClass
        val jmClass = declaringClass.takeIf { !it.isEnum }
            ?.let { cache.getJmClass(it) }
            ?: return null

        return JsonCreator.Mode.DEFAULT
            .takeIf { ann.annotated.isPrimarilyConstructorOf(jmClass) && !hasCreator(declaringClass, jmClass) }
    }
}

private fun Constructor<*>.isPrimarilyConstructorOf(jmClass: JmClass): Boolean = jmClass.findKmConstructor(this)
    ?.let { !it.isSecondary || jmClass.constructors.size == 1 }
    ?: false

private fun KmClassifier.isString(): Boolean = this is KmClassifier.Class && this.name == "kotlin/String"

private fun isPossibleSingleString(
    kotlinParams: List<KmValueParameter>,
    javaFunction: Executable,
    propertyNames: Set<String>
): Boolean = kotlinParams.size == 1 &&
    kotlinParams[0].let { it.name !in propertyNames && it.type.classifier.isString() } &&
    javaFunction.parameters[0].annotations.none { it is JsonProperty }

private fun hasCreatorConstructor(clazz: Class<*>, jmClass: JmClass, propertyNames: Set<String>): Boolean {
    val kmConstructorMap = jmClass.constructors.associateBy { it.signature?.descriptor }

    return clazz.constructors.any { constructor ->
        val kmConstructor = kmConstructorMap[constructor.toSignature().descriptor] ?: return@any false

        !isPossibleSingleString(kmConstructor.valueParameters, constructor, propertyNames) &&
            constructor.hasCreatorAnnotation()
    }
}

// In the original, `isPossibleSingleString` comparison was disabled,
// and if enabled, the behavior would have changed, so the comparison is skipped.
private fun hasCreatorFunction(clazz: Class<*>): Boolean = clazz.declaredMethods
    .any { Modifier.isStatic(it.modifiers) && it.hasCreatorAnnotation() }

private fun hasCreator(clazz: Class<*>, jmClass: JmClass): Boolean {
    val propertyNames = jmClass.propertyNameSet
    return hasCreatorConstructor(clazz, jmClass, propertyNames) || hasCreatorFunction(clazz)
}
