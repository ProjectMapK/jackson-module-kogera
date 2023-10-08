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
        private const val serialVersionUID = 3L
    }

    // This cache is used for both serialization and deserialization, so reserve a larger size from the start.
    private val classCache = LRUMap<Class<*>, JmClass>(reflectionCacheSize, reflectionCacheSize)

    // Initial size is 0 because the value class is not always used
    private val valueClassReturnTypeCache: LRUMap<AnnotatedMethod, Optional<Class<*>>> =
        LRUMap(0, reflectionCacheSize)

    // TODO: Consider whether the cache size should be reduced more,
    //       since the cache is used only twice locally at initialization per property.
    private val valueClassBoxConverterCache: LRUMap<Class<*>, ValueClassBoxConverter<*, *>> =
        LRUMap(0, reflectionCacheSize)
    private val valueClassUnboxConverterCache: LRUMap<Class<*>, ValueClassUnboxConverter<*>> =
        LRUMap(0, reflectionCacheSize)

    fun getJmClass(clazz: Class<*>): JmClass? {
        return classCache[clazz] ?: run {
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
            (classCache.putIfAbsent(clazz, value) ?: value)
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
        val method = getter.member.apply {
            // If the return value of the getter is a value class,
            // it will be serialized properly without doing anything.
            // TODO: Verify the case where a value class encompasses another value class.
            if (this.returnType.isUnboxableValueClass()) return null
        }

        val optional = valueClassReturnTypeCache.get(method)

        return if (optional != null) {
            optional
        } else {
            val value = Optional.ofNullable(method.getValueClassReturnType())
            (valueClassReturnTypeCache.putIfAbsent(getter, value) ?: value)
        }.orElse(null)
    }

    fun getValueClassBoxConverter(unboxedClass: Class<*>, valueClass: Class<*>): ValueClassBoxConverter<*, *> =
        valueClassBoxConverterCache.get(valueClass) ?: run {
            val value = ValueClassBoxConverter(unboxedClass, valueClass)

            (valueClassBoxConverterCache.putIfAbsent(valueClass, value) ?: value)
        }

    fun getValueClassUnboxConverter(valueClass: Class<*>): ValueClassUnboxConverter<*> =
        valueClassUnboxConverterCache.get(valueClass) ?: run {
            val value = ValueClassUnboxConverter(valueClass)

            (valueClassUnboxConverterCache.putIfAbsent(valueClass, value) ?: value)
        }
}
