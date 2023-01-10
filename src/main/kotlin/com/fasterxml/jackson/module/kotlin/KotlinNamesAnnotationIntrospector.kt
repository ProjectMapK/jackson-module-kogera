package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.introspect.Annotated
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor
import com.fasterxml.jackson.databind.introspect.AnnotatedField
import com.fasterxml.jackson.databind.introspect.AnnotatedMember
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector
import kotlinx.metadata.jvm.fieldSignature
import kotlinx.metadata.jvm.getterSignature
import kotlinx.metadata.jvm.setterSignature
import kotlinx.metadata.jvm.signature
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaType

internal class KotlinNamesAnnotationIntrospector(val module: KotlinModule, val cache: ReflectionCache, val ignoredClassesForImplyingJsonCreator: Set<KClass<*>>) : NopAnnotationIntrospector() {
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
        ?.takeIf { it.parameters.size == 1 /* && it.returnType == Void::class.java */ }
        ?.let { it.declaringClass.toKmClass() }
        ?.let { kmClass ->
            val methodSignature = m.annotated.toSignature()
            kmClass.properties.none { it.setterSignature == methodSignature }
        } ?: false

    @Suppress("UNCHECKED_CAST")
    private fun hasCreatorAnnotation(member: AnnotatedConstructor): Boolean {
        // don't add a JsonCreator to any constructor if one is declared already

        val kClass = cache.kotlinFromJava(member.declaringClass as Class<Any>)
            .apply { if (this in ignoredClassesForImplyingJsonCreator) return false }
        val kConstructor = cache.kotlinFromJava(member.annotated as Constructor<Any>) ?: return false

        // TODO:  should we do this check or not?  It could cause failures if we miss another way a property could be set
        // val requiredProperties = kClass.declaredMemberProperties.filter {!it.returnType.isMarkedNullable }.map { it.name }.toSet()
        // val areAllRequiredParametersInConstructor = kConstructor.parameters.all { requiredProperties.contains(it.name) }

        val propertyNames = kClass.memberProperties.map { it.name }.toSet()

        return when {
            kConstructor.isPossibleSingleString(propertyNames) -> false
            kConstructor.parameters.any { it.name == null } -> false
            !kClass.isPrimaryConstructor(kConstructor) -> false
            else -> {
                val anyConstructorHasJsonCreator = kClass.constructors
                    .filterOutSingleStringCallables(propertyNames)
                    .any { it.hasAnnotation<JsonCreator>() }

                val anyCompanionMethodIsJsonCreator = member.type.rawClass.kotlin.companionObject?.declaredFunctions
                    ?.filterOutSingleStringCallables(propertyNames)
                    ?.any { it.hasAnnotation<JsonCreator>() && it.hasAnnotation<JvmStatic>() }
                    ?: false

                !(anyConstructorHasJsonCreator || anyCompanionMethodIsJsonCreator)
            }
        }
    }

    override fun hasCreatorAnnotation(member: Annotated): Boolean =
        if (member is AnnotatedConstructor && member.isKotlinConstructorWithParameters()) {
            cache.checkConstructorIsCreatorAnnotated(member) { hasCreatorAnnotation(it) }
        } else {
            false
        }

    @Suppress("UNCHECKED_CAST")
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
}

// if has parameters, is a Kotlin class, and the parameters all have parameter annotations, then pretend we have a JsonCreator
private fun AnnotatedConstructor.isKotlinConstructorWithParameters(): Boolean =
    parameterCount > 0 && declaringClass.isKotlinClass() && !declaringClass.isEnum

private fun KFunction<*>.isPossibleSingleString(propertyNames: Set<String>): Boolean = parameters.size == 1 &&
    parameters[0].name !in propertyNames &&
    parameters[0].type.javaType == String::class.java &&
    !parameters[0].hasAnnotation<JsonProperty>()

private fun Collection<KFunction<*>>.filterOutSingleStringCallables(propertyNames: Set<String>): Collection<KFunction<*>> =
    this.filter { !it.isPossibleSingleString(propertyNames) }

private fun KClass<*>.isPrimaryConstructor(kConstructor: KFunction<*>) = this.primaryConstructor.let {
    it == kConstructor || (it == null && this.constructors.size == 1)
}
