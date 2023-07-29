package io.github.projectmapk.jackson.module.kogera

import com.fasterxml.jackson.databind.introspect.AnnotatedMethod
import com.fasterxml.jackson.databind.util.LRUMap
import io.github.projectmapk.jackson.module.kogera.deser.value_instantiator.creator.ConstructorValueCreator
import io.github.projectmapk.jackson.module.kogera.deser.value_instantiator.creator.MethodValueCreator
import io.github.projectmapk.jackson.module.kogera.deser.value_instantiator.creator.ValueCreator
import io.github.projectmapk.jackson.module.kogera.ser.ValueClassBoxConverter
import java.io.Serializable
import java.lang.reflect.Constructor
import java.lang.reflect.Executable
import java.lang.reflect.Method
import java.util.Optional

internal class ReflectionCache(reflectionCacheSize: Int) : Serializable {
    companion object {
        // Increment is required when properties that use LRUMap are changed.
        @Suppress("ConstPropertyName")
        private const val serialVersionUID = 1L
    }

    // This cache is used for both serialization and deserialization, so reserve a larger size from the start.
    private val classCache = LRUMap<Class<*>, Optional<JmClass>>(reflectionCacheSize, reflectionCacheSize)
    private val creatorCache: LRUMap<Executable, ValueCreator<*>>

    // Initial size is 0 because the value class is not always used
    private val valueClassReturnTypeCache: LRUMap<AnnotatedMethod, Optional<Class<*>>> =
        LRUMap(0, reflectionCacheSize)

    // TODO: Consider whether the cache size should be reduced more,
    //       since the cache is used only twice locally at initialization per property.
    private val valueClassBoxConverterCache: LRUMap<Class<*>, ValueClassBoxConverter<*, *>> =
        LRUMap(0, reflectionCacheSize)

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

    fun getJmClass(clazz: Class<*>): JmClass? {
        val optional = classCache.get(clazz)

        return if (optional != null) {
            optional
        } else {
            val value = Optional.ofNullable(JmClass.createOrNull(clazz))
            (classCache.putIfAbsent(clazz, value) ?: value)
        }.orElse(null)
    }

    /**
     * return null if declaringClass is not kotlin class
     */
    fun valueCreatorFromJava(creator: Executable): ValueCreator<*>? = when (creator) {
        is Constructor<*> -> {
            creatorCache.get(creator)
                ?: run {
                    getJmClass(creator.declaringClass)?.let {
                        val value = ConstructorValueCreator(creator, it.kmClass)
                        creatorCache.putIfAbsent(creator, value) ?: value
                    }
                }
        }

        is Method -> {
            creatorCache.get(creator)
                ?: run {
                    getJmClass(creator.declaringClass)?.let {
                        val value = MethodValueCreator<Any?>(creator, it.kmClass)
                        creatorCache.putIfAbsent(creator, value) ?: value
                    }
                }
        }

        else -> throw IllegalStateException(
            "Expected a constructor or method to create a Kotlin object, instead found ${creator.javaClass.name}"
        )
    } // we cannot reflect this method so do the default Java-ish behavior

    private fun AnnotatedMethod.getValueClassReturnType(): Class<*>? {
        val getter = this.member.apply {
            // If the return value of the getter is a value class,
            // it will be serialized properly without doing anything.
            // TODO: Verify the case where a value class encompasses another value class.
            if (this.returnType.isUnboxableValueClass()) return null
        }
        val kotlinProperty = getJmClass(getter.declaringClass)?.findPropertyByGetter(getter)

        // Since there was no way to directly determine whether returnType is a value class or not,
        // Class is restored and processed.
        return kotlinProperty?.returnType?.reconstructClassOrNull()?.takeIf { it.isUnboxableValueClass() }
    }

    fun findValueClassReturnType(getter: AnnotatedMethod): Class<*>? {
        val optional = valueClassReturnTypeCache.get(getter)

        return if (optional != null) {
            optional
        } else {
            val value = Optional.ofNullable(getter.getValueClassReturnType())
            (valueClassReturnTypeCache.putIfAbsent(getter, value) ?: value)
        }.orElse(null)
    }

    fun getValueClassBoxConverter(unboxedClass: Class<*>, valueClass: Class<*>): ValueClassBoxConverter<*, *> =
        valueClassBoxConverterCache.get(valueClass) ?: run {
            val value = ValueClassBoxConverter(unboxedClass, valueClass)

            (valueClassBoxConverterCache.putIfAbsent(valueClass, value) ?: value)
        }
}
