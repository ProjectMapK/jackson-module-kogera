package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.introspect.Annotated
import com.fasterxml.jackson.databind.introspect.AnnotatedField
import com.fasterxml.jackson.databind.introspect.AnnotatedMember
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector
import com.fasterxml.jackson.databind.jsontype.NamedType
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.module.kotlin.ser.serializers.ValueClassBoxSerializer
import com.fasterxml.jackson.module.kotlin.ser.serializers.ValueClassStaticJsonValueSerializer
import kotlinx.metadata.Flag
import kotlinx.metadata.KmClassifier
import kotlinx.metadata.KmProperty
import kotlinx.metadata.jvm.fieldSignature
import kotlinx.metadata.jvm.getterSignature
import kotlinx.metadata.jvm.setterSignature
import kotlinx.metadata.jvm.signature
import java.lang.reflect.AccessibleObject
import java.lang.reflect.Constructor
import java.lang.reflect.Method

internal class KotlinAnnotationIntrospector(
    private val context: Module.SetupContext,
    private val cache: ReflectionCache,
    private val nullToEmptyCollection: Boolean,
    private val nullToEmptyMap: Boolean,
    private val nullIsSameAsDefault: Boolean
) : NopAnnotationIntrospector() {

    // TODO: implement nullIsSameAsDefault flag, which represents when TRUE that if something has a default value,
    //       it can be passed a null to default it
    //       this likely impacts this class to be accurate about what COULD be considered required

    override fun hasRequiredMarker(m: AnnotatedMember): Boolean? {
        val hasRequired = cache.javaMemberIsRequired(m) {
            try {
                when {
                    nullToEmptyCollection && m.type.isCollectionLikeType -> false
                    nullToEmptyMap && m.type.isMapLikeType -> false
                    m.member.declaringClass.isKotlinClass() -> when (m) {
                        is AnnotatedField -> m.hasRequiredMarker()
                        is AnnotatedMethod -> m.getRequiredMarkerFromCorrespondingAccessor()
                        is AnnotatedParameter -> m.hasRequiredMarker()
                        else -> null
                    }
                    else -> null
                }
            } catch (ex: UnsupportedOperationException) {
                null
            }
        }
        return hasRequired
    }

    // Find a serializer to handle the case where the getter returns an unboxed value from the value class.
    override fun findSerializer(am: Annotated): StdSerializer<*>? = when (am) {
        is AnnotatedMethod -> {
            val getter = am.member.apply {
                // If the return value of the getter is a value class,
                // it will be serialized properly without doing anything.
                if (this.returnType.isUnboxableValueClass()) return null
            }
            val getterSignature = getter.toSignature()

            val kotlinProperty =
                getter.declaringClass.toKmClass()?.properties?.find { it.getterSignature == getterSignature }

            (kotlinProperty?.returnType?.classifier as? KmClassifier.Class)?.let { classifier ->
                // Since there was no way to directly determine whether returnType is a value class or not,
                // Class is restored and processed.
                // If the cost of this process is significant, consider caching it.
                runCatching {
                    // Kotlin-specific types such as kotlin.String will cause an error,
                    // but value classes will not cause an error, so ignore them
                    Class.forName(classifier.name.replace(".", "$").replace("/", "."))
                }
                    .getOrNull()
                    ?.takeIf { it.annotations.any { ann -> ann is JvmInline } }
                    ?.let { outerClazz ->
                        val innerClazz = getter.returnType

                        ValueClassStaticJsonValueSerializer.createdOrNull(outerClazz, innerClazz)
                            ?: @Suppress("UNCHECKED_CAST") (ValueClassBoxSerializer(outerClazz, innerClazz))
                    }
            }
        }
        // Ignore the case of AnnotatedField, because JvmField cannot be set in the field of value class.
        else -> null
    }

    // Perform proper serialization even if the value wrapped by the value class is null.
    override fun findNullSerializer(am: Annotated) = findSerializer(am)

    /**
     * Subclasses can be detected automatically for sealed classes, since all possible subclasses are known
     * at compile-time to Kotlin. This makes [com.fasterxml.jackson.annotation.JsonSubTypes] redundant.
     */
    override fun findSubtypes(a: Annotated): MutableList<NamedType>? = a.rawType
        .takeIf { it.isKotlinClass() }
        ?.let { rawType ->
            rawType.kotlin.sealedSubclasses
                .map { NamedType(it.java) }
                .toMutableList()
                .ifEmpty { null }
        }

    private fun AnnotatedField.hasRequiredMarker(): Boolean? {
        val member = annotated

        val byAnnotation = member.isRequiredByAnnotation()
        val fieldSignature = member.toSignature()
        val byNullability = member.declaringClass.toKmClass()
            ?.properties
            ?.find { it.fieldSignature == fieldSignature }
            ?.let { !Flag.Type.IS_NULLABLE(it.returnType.flags) }

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

    private fun KmProperty.isRequiredByNullability(): Boolean = !Flag.Type.IS_NULLABLE(this.returnType.flags)

    private fun AnnotatedMethod.getRequiredMarkerFromCorrespondingAccessor(): Boolean? {
        val memberSignature = member.toSignature()
        member.declaringClass.toKmClass()?.properties?.forEach { kmProperty ->
            if (kmProperty.getterSignature == memberSignature || kmProperty.setterSignature == memberSignature) {
                val byAnnotation = this.member.isRequiredByAnnotation()
                val byNullability = kmProperty.isRequiredByNullability()
                return requiredAnnotationOrNullability(byAnnotation, byNullability)
            }
        }
        return null
    }

    private fun AnnotatedParameter.hasRequiredMarker(): Boolean? {
        val byAnnotation = this.getAnnotation(JsonProperty::class.java)?.required
        val byNullability: Boolean? = member.declaringClass.toKmClass()?.let { kmClass ->
            when (val member = member) {
                is Constructor<*> -> {
                    val signature = member.toSignature()
                    val paramDef = kmClass.constructors.find { it.signature?.desc == signature.desc }
                        ?.let { it.valueParameters[index] }
                        ?: return@let null

                    paramDef to member.parameterTypes[index]
                }
                is Method -> {
                    val signature = member.toSignature()
                    val paramDef = kmClass.functions.find { it.signature == signature }
                        ?.let { it.valueParameters[index] }
                        ?: return@let null

                    paramDef to member.parameterTypes[index]
                }
                else -> null
            }?.let { (paramDef, paramType) ->
                val isPrimitive = paramType.isPrimitive
                val isOptional = Flag.ValueParameter.DECLARES_DEFAULT_VALUE(paramDef.flags)
                val isMarkedNullable = Flag.Type.IS_NULLABLE(paramDef.type.flags)

                !isMarkedNullable && !isOptional &&
                    !(isPrimitive && !context.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES))
            }
        }

        return requiredAnnotationOrNullability(byAnnotation, byNullability)
    }
}
