package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams
import com.fasterxml.jackson.databind.util.LRUMap
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.creator.ConstructorValueCreator
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.creator.MethodValueCreator
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.creator.ValueCreator
import java.lang.reflect.Method

internal class ReflectionCache(reflectionCacheSize: Int) {
    private val creatorCache: LRUMap<Any, ValueCreator<*>>

    init {
        // The current default value of reflectionCacheSize is 512.
        // If less than 512 is specified, initialEntries shall be half of the reflectionCacheSize,
        // since it is assumed that the situation does not require a large amount of space from the beginning.
        // Conversely, if reflectionCacheSize is increased,
        // the amount of cache is considered to be a performance bottleneck,
        // and therefore reflectionCacheSize is used as is for initialEntries.
        val initialEntries = if (reflectionCacheSize <= KotlinModule.Builder.reflectionCacheSizeDefault) {
            reflectionCacheSize / 2
        } else {
            reflectionCacheSize
        }

        creatorCache = LRUMap<Any, ValueCreator<*>>(initialEntries, reflectionCacheSize)
    }

    /**
     * return null if declaringClass is not kotlin class
     */
    fun valueCreatorFromJava(withArgsCreator: AnnotatedWithParams): ValueCreator<*>? {
        val kmClass = withArgsCreator.declaringClass.toKmClass() ?: return null

        return when (withArgsCreator) {
            is AnnotatedConstructor -> {
                val constructor = withArgsCreator.annotated

                creatorCache.get(constructor)
                    ?: run {
                        val value = ConstructorValueCreator(constructor, kmClass)
                        creatorCache.putIfAbsent(constructor, value) ?: value
                    }
            }

            is AnnotatedMethod -> {
                val method = withArgsCreator.annotated as Method

                creatorCache.get(method)
                    ?: run {
                        val value = MethodValueCreator<Any?>(method, kmClass)
                        creatorCache.putIfAbsent(method, value) ?: value
                    }
            }

            else -> throw IllegalStateException(
                "Expected a constructor or method to create a Kotlin object," +
                    " instead found ${withArgsCreator.annotated.javaClass.name}"
            )
        }
    } // we cannot reflect this method so do the default Java-ish behavior
}
