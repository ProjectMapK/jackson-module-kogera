package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams
import com.fasterxml.jackson.databind.util.LRUMap
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.creator.ConstructorValueCreator
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.creator.MethodValueCreator
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.creator.ValueCreator
import java.lang.reflect.Constructor
import java.lang.reflect.Method

internal class ReflectionCache(reflectionCacheSize: Int) {
    private val javaConstructorToValueCreator = LRUMap<Constructor<*>, ConstructorValueCreator<*>>(reflectionCacheSize, reflectionCacheSize)
    private val javaMethodToValueCreator = LRUMap<Method, MethodValueCreator<*>>(reflectionCacheSize, reflectionCacheSize)

    /**
     * return null if...
     * - can't get kotlinFunction
     * - contains extensionReceiverParameter
     * - instance parameter is not companion object or can't get
     */
    fun valueCreatorFromJava(withArgsCreator: AnnotatedWithParams): ValueCreator<*> = when (withArgsCreator) {
        is AnnotatedConstructor -> {
            val constructor = withArgsCreator.annotated

            javaConstructorToValueCreator.get(constructor)
                ?: run {
                    val value = ConstructorValueCreator(constructor)
                    javaConstructorToValueCreator.putIfAbsent(constructor, value) ?: value
                }
        }
        is AnnotatedMethod -> {
            val method = withArgsCreator.annotated as Method

            javaMethodToValueCreator.get(method)
                ?: kotlin.run {
                    val value = MethodValueCreator<Any?>(method)
                    javaMethodToValueCreator.putIfAbsent(method, value) ?: value
                }
        }
        else -> throw IllegalStateException("Expected a constructor or method to create a Kotlin object, instead found ${withArgsCreator.annotated.javaClass.name}")
    } // we cannot reflect this method so do the default Java-ish behavior
}
