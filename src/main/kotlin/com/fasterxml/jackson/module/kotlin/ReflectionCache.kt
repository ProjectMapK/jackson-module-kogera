package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.databind.util.LRUMap
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.creator.ConstructorValueCreator
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.creator.MethodValueCreator
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.creator.ValueCreator
import kotlinx.metadata.KmClass
import java.lang.reflect.Constructor
import java.lang.reflect.Executable
import java.lang.reflect.Method
import java.util.Optional

internal class ReflectionCache(reflectionCacheSize: Int) {
    // This cache is used for both serialization and deserialization, so reserve a larger size from the start.
    private val classCache = LRUMap<Class<*>, Optional<KmClass>>(reflectionCacheSize, reflectionCacheSize)
    private val creatorCache: LRUMap<Executable, ValueCreator<*>>

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
        creatorCache = LRUMap(initialEntries, reflectionCacheSize)
    }

    fun getKmClass(clazz: Class<*>): KmClass? {
        val optional = classCache.get(clazz)

        return if (optional != null) {
            optional
        } else {
            val value = Optional.ofNullable(clazz.toKmClass())
            (classCache.putIfAbsent(clazz, value) ?: value)
        }.orElse(null)
    }

    /**
     * return null if declaringClass is not kotlin class
     */
    fun valueCreatorFromJava(_creator: Executable): ValueCreator<*>? = when (val creator = _creator) {
        is Constructor<*> -> {
            creatorCache.get(creator)
                ?: run {
                    getKmClass(creator.declaringClass)?.let {
                        val value = ConstructorValueCreator(creator, it)
                        creatorCache.putIfAbsent(creator, value) ?: value
                    }
                }
        }

        is Method -> {
            creatorCache.get(creator)
                ?: run {
                    getKmClass(creator.declaringClass)?.let {
                        val value = MethodValueCreator<Any?>(creator, it)
                        creatorCache.putIfAbsent(creator, value) ?: value
                    }
                }
        }

        else -> throw IllegalStateException(
            "Expected a constructor or method to create a Kotlin object, instead found ${creator.javaClass.name}"
        )
    } // we cannot reflect this method so do the default Java-ish behavior
}
