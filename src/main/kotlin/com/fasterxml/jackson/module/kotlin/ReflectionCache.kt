package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.databind.introspect.AnnotatedMethod
import com.fasterxml.jackson.databind.util.LRUMap
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.creator.ConstructorValueCreator
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.creator.MethodValueCreator
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.creator.ValueCreator
import com.fasterxml.jackson.module.kotlin.ser.ValueClassBoxConverter
import kotlinx.metadata.KmClass
import java.lang.reflect.Constructor
import java.lang.reflect.Executable
import java.lang.reflect.Method
import java.util.Optional

internal class ReflectionCache(reflectionCacheSize: Int) {
    // This cache is used for both serialization and deserialization, so reserve a larger size from the start.
    private val classCache = LRUMap<Class<*>, Optional<KmClass>>(reflectionCacheSize, reflectionCacheSize)
    private val creatorCache: LRUMap<Executable, ValueCreator<*>>

    // TODO: Consider whether the cache size should be reduced more,
    //       since the cache is used only twice locally at initialization per property.
    private val valueClassBoxConverterCache: LRUMap<AnnotatedMethod, Optional<ValueClassBoxConverter<*, *>>>

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
        valueClassBoxConverterCache = LRUMap(initialEntries, reflectionCacheSize)
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

    private fun AnnotatedMethod.findValueClassBoxConverter(): ValueClassBoxConverter<*, *>? {
        val getter = this.member.apply {
            // If the return value of the getter is a value class,
            // it will be serialized properly without doing anything.
            // TODO: Verify the case where a value class encompasses another value class.
            if (this.returnType.isUnboxableValueClass()) return null
        }
        val kotlinProperty = getKmClass(getter.declaringClass)?.findPropertyByGetter(getter)

        // Since there was no way to directly determine whether returnType is a value class or not,
        // Class is restored and processed.
        return kotlinProperty?.returnType?.reconstructClassOrNull()?.let { clazz ->
            clazz.takeIf { it.isUnboxableValueClass() }
                ?.let { ValueClassBoxConverter(getter.returnType, it) }
        }
    }

    fun findValueClassBoxConverterFrom(a: AnnotatedMethod): ValueClassBoxConverter<*, *>? {
        val optional = valueClassBoxConverterCache.get(a)

        return if (optional != null) {
            optional
        } else {
            val value = Optional.ofNullable(a.findValueClassBoxConverter())
            (valueClassBoxConverterCache.putIfAbsent(a, value) ?: value)
        }.orElse(null)
    }
}
