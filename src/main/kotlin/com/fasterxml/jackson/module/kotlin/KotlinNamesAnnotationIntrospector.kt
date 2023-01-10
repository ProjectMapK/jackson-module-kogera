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
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.internal.KotlinReflectionInternalError
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.kotlinFunction

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
        return if (param.declaringClass.isKotlinClass()) {
            val member = param.owner.member
            if (member is Constructor<*>) {
                val ctor = (member as Constructor<Any>)
                val ctorParmCount = ctor.parameterTypes.size
                val ktorParmCount = try { ctor.kotlinFunction?.parameters?.size ?: 0 } catch (ex: KotlinReflectionInternalError) { 0 } catch (ex: UnsupportedOperationException) { 0 }
                if (ktorParmCount > 0 && ktorParmCount == ctorParmCount) {
                    ctor.kotlinFunction?.parameters?.get(param.index)?.name
                } else {
                    null
                }
            } else if (member is Method) {
                try {
                    val temp = member.kotlinFunction

                    val firstParamKind = temp?.parameters?.firstOrNull()?.kind
                    val idx = if (firstParamKind != KParameter.Kind.VALUE) param.index + 1 else param.index
                    val parmCount = temp?.parameters?.size ?: 0
                    if (parmCount > idx) {
                        temp?.parameters?.get(idx)?.name
                    } else {
                        null
                    }
                } catch (ex: KotlinReflectionInternalError) {
                    null
                }
            } else {
                null
            }
        } else {
            null
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
