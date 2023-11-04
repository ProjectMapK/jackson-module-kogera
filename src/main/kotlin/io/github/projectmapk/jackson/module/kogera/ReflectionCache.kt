package io.github.projectmapk.jackson.module.kogera

import com.fasterxml.jackson.databind.util.LRUMap
import java.io.Serializable
import java.lang.reflect.Method
import java.util.Optional

// For ease of testing, maxCacheSize is limited only in KotlinModule.
internal class ReflectionCache(initialCacheSize: Int, maxCacheSize: Int) : Serializable {
    companion object {
        // Increment is required when properties that use LRUMap are changed.
        @Suppress("ConstPropertyName")
        private const val serialVersionUID = 4L
    }

    /**
     * For frequently used JmClass and BoxedReturnType, reduce overhead by using Class and Method directly as key.
     * For other caches, if the key type overlaps, wrap it.
     */
    private sealed class OtherCacheKey<K : Any, V : Any> {
        abstract val key: K

        // The comparison was implemented directly because the decompiled results showed subtle efficiency.

        final override fun equals(other: Any?): Boolean =
            (other as? OtherCacheKey<*, *>)?.let { it::class == this::class && it.key == key } ?: false

        // If the hashCode matches the raw key, the search efficiency is reduced, so it is displaced.
        final override fun hashCode(): Int = key.hashCode() * 31
        final override fun toString(): String = key.toString()

        class ValueClassBoxConverter(
            override val key: Class<*>
        ) : OtherCacheKey<Class<*>, io.github.projectmapk.jackson.module.kogera.ValueClassBoxConverter<*, *>>()
        class ValueClassUnboxConverter(
            override val key: Class<*>
        ) : OtherCacheKey<Class<*>, io.github.projectmapk.jackson.module.kogera.ValueClassUnboxConverter<*>>()
    }

    private val cache = LRUMap<Any, Any>(initialCacheSize, maxCacheSize)
    private fun <T : Any> find(key: Any): T? = cache[key]?.let {
        @Suppress("UNCHECKED_CAST")
        it as T
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> putIfAbsent(key: Any, value: T): T = cache.putIfAbsent(key, value) as T? ?: value

    fun getJmClass(clazz: Class<*>): JmClass? {
        return find(clazz) ?: run {
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
            putIfAbsent(clazz, value)
        }
    }

    private fun Method.getValueClassReturnType(): Class<*>? {
        val kotlinProperty = getJmClass(declaringClass)?.findPropertyByGetter(this)

        // Since there was no way to directly determine whether returnType is a value class or not,
        // Class is restored and processed.
        return kotlinProperty?.returnType?.reconstructClassOrNull()?.takeIf { it.isUnboxableValueClass() }
    }

    // Return boxed type on Kotlin for unboxed getters
    fun findBoxedReturnType(getter: Method): Class<*>? {
        val optional = find<Optional<Class<*>>>(getter)

        return if (optional != null) {
            optional
        } else {
            // If the return value of the getter is a value class,
            // it will be serialized properly without doing anything.
            // TODO: Verify the case where a value class encompasses another value class.
            if (getter.returnType.isUnboxableValueClass()) return null

            val value = Optional.ofNullable(getter.getValueClassReturnType())
            putIfAbsent(getter, value)
        }.orElse(null)
    }

    fun getValueClassBoxConverter(unboxedClass: Class<*>, valueClass: Class<*>): ValueClassBoxConverter<*, *> {
        val key = OtherCacheKey.ValueClassBoxConverter(valueClass)
        return find(key) ?: putIfAbsent(key, ValueClassBoxConverter(unboxedClass, valueClass))
    }

    fun getValueClassUnboxConverter(valueClass: Class<*>): ValueClassUnboxConverter<*> {
        val key = OtherCacheKey.ValueClassUnboxConverter(valueClass)
        return find(key) ?: putIfAbsent(key, ValueClassUnboxConverter(valueClass))
    }
}
