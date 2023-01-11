package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.cfg.MapperConfig
import com.fasterxml.jackson.databind.introspect.Annotated
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor
import com.fasterxml.jackson.databind.introspect.AnnotatedField
import com.fasterxml.jackson.databind.introspect.AnnotatedMember
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector
import kotlinx.metadata.Flag
import kotlinx.metadata.KmClass
import kotlinx.metadata.KmClassifier
import kotlinx.metadata.KmValueParameter
import kotlinx.metadata.jvm.fieldSignature
import kotlinx.metadata.jvm.getterSignature
import kotlinx.metadata.jvm.setterSignature
import kotlinx.metadata.jvm.signature
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Constructor
import java.lang.reflect.Executable
import java.lang.reflect.Method

internal class KotlinNamesAnnotationIntrospector constructor(
    val module: KotlinModule,
    val cache: ReflectionCache
) : NopAnnotationIntrospector() {
    // since 2.4
    override fun findImplicitPropertyName(member: AnnotatedMember): String? = when (member) {
        is AnnotatedMethod -> member.annotated.declaringClass.toKmClass()?.let { kmClass ->
            val methodSignature = member.annotated.toSignature()

            kmClass.properties.find { it.getterSignature == methodSignature }?.name
        }
        is AnnotatedField -> member.annotated.declaringClass.toKmClass()?.let { kmClass ->
            val fieldSignature = member.annotated.toSignature()

            kmClass.properties.find { it.fieldSignature == fieldSignature }?.name
        }
        is AnnotatedParameter -> findKotlinParameterName(member)
        else -> null
    }

    private fun findKotlinParameterName(param: AnnotatedParameter): String? {
        val declaringClass = param.declaringClass

        return declaringClass.toKmClass()?.let { kmClass ->
            when (val member = param.owner.member) {
                is Constructor<*> -> {
                    val signature = member.toSignature()

                    kmClass.constructors.find { it.signature?.desc == signature.desc }
                        ?.let { it.valueParameters[param.index].name }
                }
                is Method -> {
                    val companionKmClass = declaringClass.getDeclaredField(kmClass.companionObject!!)
                        .type
                        .toKmClass()!!
                    val signature = member.toSignature()

                    companionKmClass.functions.find { it.signature == signature }
                        ?.let { it.valueParameters[param.index].name }
                }
                else -> null
            }
        }
    }

    // If it is not a property on Kotlin, it is not used to serialization
    override fun findPropertyAccess(ann: Annotated): JsonProperty.Access? = when (ann) {
        is AnnotatedMethod ->
            ann.annotated
                .takeIf { it.parameters.isEmpty() } // Ignore target is only getter
                ?.let { method ->
                    method.declaringClass.toKmClass()?.let { kmClass ->
                        val methodSignature = method.toSignature()

                        JsonProperty.Access.WRITE_ONLY.takeIf {
                            kmClass.properties.none { it.getterSignature == methodSignature }
                        }
                    }
                }
        else -> null
    }

    override fun hasIgnoreMarker(m: AnnotatedMember): Boolean = (m as? AnnotatedMethod)?.member
        ?.takeIf { it.parameters.size == 1 }
        ?.let { it.declaringClass.toKmClass() }
        ?.let { kmClass ->
            val methodSignature = m.annotated.toSignature()
            kmClass.properties.none { it.setterSignature == methodSignature }
        } ?: false

    override fun findCreatorAnnotation(config: MapperConfig<*>, ann: Annotated): JsonCreator.Mode? {
        (ann as? AnnotatedConstructor)?.takeIf { 0 < it.parameterCount } ?: return null

        val declaringClass = ann.declaringClass
        val kmClass = declaringClass
            ?.takeIf { !it.isEnum }
            ?.toKmClass()
            ?: return null

        return JsonCreator.Mode.DEFAULT
            .takeIf { ann.annotated.isPrimarilyConstructorOf(kmClass) && !hasCreator(declaringClass, kmClass) }
    }
}

private fun Constructor<*>.isPrimarilyConstructorOf(kmClass: KmClass): Boolean {
    val signature = this.toSignature()

    return kmClass.constructors
        .find { it.signature?.desc == signature.desc }
        ?.let { !Flag.Constructor.IS_SECONDARY(it.flags) || kmClass.constructors.size == 1 }
        ?: false
}

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
