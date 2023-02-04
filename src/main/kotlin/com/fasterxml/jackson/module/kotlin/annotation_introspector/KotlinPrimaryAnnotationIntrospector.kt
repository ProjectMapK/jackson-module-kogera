package com.fasterxml.jackson.module.kotlin.annotation_introspector

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.cfg.MapperConfig
import com.fasterxml.jackson.databind.introspect.Annotated
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor
import com.fasterxml.jackson.databind.introspect.AnnotatedField
import com.fasterxml.jackson.databind.introspect.AnnotatedMember
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector
import com.fasterxml.jackson.databind.jsontype.NamedType
import com.fasterxml.jackson.module.kotlin.ReflectionCache
import com.fasterxml.jackson.module.kotlin.findKmConstructor
import com.fasterxml.jackson.module.kotlin.isNullable
import com.fasterxml.jackson.module.kotlin.reconstructClass
import com.fasterxml.jackson.module.kotlin.toSignature
import kotlinx.metadata.Flag
import kotlinx.metadata.KmClass
import kotlinx.metadata.KmClassifier
import kotlinx.metadata.KmProperty
import kotlinx.metadata.KmValueParameter
import kotlinx.metadata.jvm.fieldSignature
import kotlinx.metadata.jvm.getterSignature
import kotlinx.metadata.jvm.setterSignature
import kotlinx.metadata.jvm.signature
import java.lang.reflect.AccessibleObject
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Constructor
import java.lang.reflect.Executable
import java.lang.reflect.Method

// AnnotationIntrospector that overrides the behavior of the default AnnotationIntrospector
// (in most cases, JacksonAnnotationIntrospector).
// Original name: KotlinAnnotationIntrospector
internal class KotlinPrimaryAnnotationIntrospector(
    private val context: Module.SetupContext,
    private val nullToEmptyCollection: Boolean,
    private val nullToEmptyMap: Boolean,
    private val cache: ReflectionCache
) : NopAnnotationIntrospector() {

    // TODO: implement nullIsSameAsDefault flag, which represents when TRUE that if something has a default value,
    //       it can be passed a null to default it
    //       this likely impacts this class to be accurate about what COULD be considered required

    override fun hasRequiredMarker(m: AnnotatedMember): Boolean? = try {
        when {
            nullToEmptyCollection && m.type.isCollectionLikeType -> false
            nullToEmptyMap && m.type.isMapLikeType -> false
            else -> cache.getKmClass(m.member.declaringClass)?.let {
                when (m) {
                    is AnnotatedField -> m.hasRequiredMarker(it)
                    is AnnotatedMethod -> m.getRequiredMarkerFromCorrespondingAccessor(it)
                    is AnnotatedParameter -> m.hasRequiredMarker(it)
                    else -> null
                }
            }
        }
    } catch (ex: UnsupportedOperationException) {
        null
    }

    private fun AnnotatedField.hasRequiredMarker(kmClass: KmClass): Boolean? {
        val member = annotated

        val byAnnotation = member.isRequiredByAnnotation()
        val fieldSignature = member.toSignature()
        val byNullability = kmClass.properties
            .find { it.fieldSignature == fieldSignature }
            ?.let { !it.returnType.isNullable() }

        return requiredAnnotationOrNullability(byAnnotation, byNullability)
    }

    private fun AccessibleObject.isRequiredByAnnotation(): Boolean? = annotations
        .filterIsInstance<JsonProperty>()
        .firstOrNull()
        ?.required

    private fun requiredAnnotationOrNullability(byAnnotation: Boolean?, byNullability: Boolean?): Boolean? = when {
        byAnnotation != null && byNullability != null -> byAnnotation || byNullability
        byNullability != null -> byNullability
        else -> byAnnotation
    }

    private fun KmProperty.isRequiredByNullability(): Boolean = !this.returnType.isNullable()

    private fun AnnotatedMethod.getRequiredMarkerFromCorrespondingAccessor(kmClass: KmClass): Boolean? {
        val memberSignature = member.toSignature()
        kmClass.properties.forEach { kmProperty ->
            if (kmProperty.getterSignature == memberSignature || kmProperty.setterSignature == memberSignature) {
                val byAnnotation = this.member.isRequiredByAnnotation()
                val byNullability = kmProperty.isRequiredByNullability()
                return requiredAnnotationOrNullability(byAnnotation, byNullability)
            }
        }
        return null
    }

    private fun AnnotatedParameter.hasRequiredMarker(kmClass: KmClass): Boolean? {
        val byAnnotation = this.getAnnotation(JsonProperty::class.java)?.required
        val byNullability: Boolean? = when (val member = member) {
            is Constructor<*> -> {
                val paramDef = kmClass.findKmConstructor(member)
                    ?.let { it.valueParameters[index] }
                    ?: return null

                paramDef to member.parameterTypes[index]
            }
            is Method -> {
                val signature = member.toSignature()
                val paramDef = kmClass.functions.find { it.signature == signature }
                    ?.let { it.valueParameters[index] }
                    ?: return null

                paramDef to member.parameterTypes[index]
            }
            else -> null
        }?.let { (paramDef, paramType) ->
            val isPrimitive = paramType.isPrimitive
            val isOptional = Flag.ValueParameter.DECLARES_DEFAULT_VALUE(paramDef.flags)
            val isMarkedNullable = paramDef.type.isNullable()

            !isMarkedNullable && !isOptional &&
                !(isPrimitive && !context.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES))
        }

        return requiredAnnotationOrNullability(byAnnotation, byNullability)
    }

    /**
     * Subclasses can be detected automatically for sealed classes, since all possible subclasses are known
     * at compile-time to Kotlin. This makes [com.fasterxml.jackson.annotation.JsonSubTypes] redundant.
     */
    // The definition location was not changed from kotlin-module because
    // the result was the same whether it was defined in Primary or Fallback.
    override fun findSubtypes(a: Annotated): List<NamedType>? = cache.getKmClass(a.rawType)?.let { kmClass ->
        kmClass.sealedSubclasses.map { NamedType(it.reconstructClass()) }.ifEmpty { null }
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
        val kmClass = declaringClass
            ?.takeIf { !it.isEnum }
            ?.let { cache.getKmClass(it) }
            ?: return null

        return JsonCreator.Mode.DEFAULT
            .takeIf { ann.annotated.isPrimarilyConstructorOf(kmClass) && !hasCreator(declaringClass, kmClass) }
    }
}

private fun Constructor<*>.isPrimarilyConstructorOf(kmClass: KmClass): Boolean = kmClass.findKmConstructor(this)
    ?.let { !Flag.Constructor.IS_SECONDARY(it.flags) || kmClass.constructors.size == 1 }
    ?: false

private fun AnnotatedElement.hasCreatorAnnotation(): Boolean =
    annotations.any { it is JsonCreator && it.mode != JsonCreator.Mode.DISABLED }

private fun KmClassifier.isString(): Boolean = this is KmClassifier.Class && this.name == "kotlin/String"

private fun isPossibleSingleString(
    kotlinParams: List<KmValueParameter>,
    javaFunction: Executable,
    propertyNames: Set<String>
): Boolean = kotlinParams.size == 1 &&
    kotlinParams[0].let { it.name !in propertyNames && it.type.classifier.isString() } &&
    javaFunction.parameters[0].annotations.none { it is JsonProperty }

private fun hasCreatorConstructor(clazz: Class<*>, kmClass: KmClass, propertyNames: Set<String>): Boolean {
    val kmConstructorMap = kmClass.constructors.associateBy { it.signature?.desc }

    return clazz.constructors.any { constructor ->
        val kmConstructor = kmConstructorMap[constructor.toSignature().desc] ?: return@any false

        !isPossibleSingleString(kmConstructor.valueParameters, constructor, propertyNames) &&
            constructor.hasCreatorAnnotation()
    }
}

// In the original, `isPossibleSingleString` comparison was disabled,
// and if enabled, the behavior would have changed, so the comparison is skipped.
private fun hasCreatorFunction(clazz: Class<*>, kmClass: KmClass): Boolean = kmClass.companionObject
    ?.let { companion ->
        clazz.getDeclaredField(companion).type.declaredMethods.any { it.hasCreatorAnnotation() }
    } ?: false

private fun hasCreator(clazz: Class<*>, kmClass: KmClass): Boolean {
    val propertyNames = kmClass.properties.map { it.name }.toSet()
    return hasCreatorConstructor(clazz, kmClass, propertyNames) || hasCreatorFunction(clazz, kmClass)
}
