package io.github.projectmapk.jackson.module.kogera

import com.fasterxml.jackson.databind.introspect.AnnotatedMethod
import com.fasterxml.jackson.databind.util.LRUMap
import java.io.Serializable
import java.lang.reflect.Method
import java.util.Optional

internal class ReflectionCache(reflectionCacheSize: Int) : Serializable {
    companion object {
        // Increment is required when properties that use LRUMap are changed.
        @Suppress("ConstPropertyName")
        private const val serialVersionUID = 4L
    }

    private sealed class CacheKey<K : Any, V : Any> {
        abstract val key: K

        final override fun equals(other: Any?): Boolean =
            (other as? CacheKey<*, *>)?.let { it::class == this::class && it.key == key } ?: false

        final override fun hashCode(): Int = key.hashCode()
        final override fun toString(): String = key.toString()

        class JmClass(
            override val key: Class<*>
        ) : CacheKey<Class<*>, io.github.projectmapk.jackson.module.kogera.JmClass>()
        class ValueClassReturnType(override val key: Method) : CacheKey<Method, Optional<Class<*>>>()
        class ValueClassBoxConverter(
            override val key: Class<*>
        ) : CacheKey<Class<*>, io.github.projectmapk.jackson.module.kogera.ValueClassBoxConverter<*, *>>()
        class ValueClassUnboxConverter(
            override val key: Class<*>
        ) : CacheKey<Class<*>, io.github.projectmapk.jackson.module.kogera.ValueClassUnboxConverter<*>>()
    }

    private val cache = LRUMap<CacheKey<*, *>, Any>(reflectionCacheSize, reflectionCacheSize)
    private fun <T : Any> find(key: CacheKey<*, T>): T? = cache[key]?.let {
        @Suppress("UNCHECKED_CAST")
        it as T
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> putIfAbsent(key: CacheKey<*, T>, value: T): T = cache.putIfAbsent(key, value) as T? ?: value

    fun getJmClass(clazz: Class<*>): JmClass? {
        val key = CacheKey.JmClass(clazz)

        return find(key) ?: run {
            val metadata = clazz.getAnnotation(Metadata::class.java) ?: return null

            // Do not parse super class for interfaces.
            val superJmClass = if (!clazz.isInterface) {
                clazz.superclass?.let {
                    // Stop parsing when `Object` is reached
                    if (it != Any::class.java) getJmClass(it) else null
                }
            } else {
                null
            }
            val interfaceJmClasses = clazz.interfaces.mapNotNull { getJmClass(it) }

            val value = JmClass(clazz, metadata, superJmClass, interfaceJmClasses)
            putIfAbsent(key, value)
        }
    }

    private fun Method.getValueClassReturnType(): Class<*>? {
        val kotlinProperty = getJmClass(declaringClass)?.findPropertyByGetter(this)

        // Since there was no way to directly determine whether returnType is a value class or not,
        // Class is restored and processed.
        return kotlinProperty?.returnType?.reconstructClassOrNull()?.takeIf { it.isUnboxableValueClass() }
    }

    // Return boxed type on Kotlin for unboxed getters
    fun findBoxedReturnType(getter: AnnotatedMethod): Class<*>? {
        val method = getter.member
        val key = CacheKey.ValueClassReturnType(method)
        val optional = find(key)

        return if (optional != null) {
            optional
        } else {
            // If the return value of the getter is a value class,
            // it will be serialized properly without doing anything.
            // TODO: Verify the case where a value class encompasses another value class.
            if (method.returnType.isUnboxableValueClass()) return null

            val value = Optional.ofNullable(method.getValueClassReturnType())
            putIfAbsent(key, value)
        }.orElse(null)
    }

    fun getValueClassBoxConverter(unboxedClass: Class<*>, valueClass: Class<*>): ValueClassBoxConverter<*, *> {
        val key = CacheKey.ValueClassBoxConverter(valueClass)
        return find(key) ?: putIfAbsent(key, ValueClassBoxConverter(unboxedClass, valueClass))
    }

    fun getValueClassUnboxConverter(valueClass: Class<*>): ValueClassUnboxConverter<*> {
        val key = CacheKey.ValueClassUnboxConverter(valueClass)
        return find(key) ?: putIfAbsent(key, ValueClassUnboxConverter(valueClass))
    }
}
