package com.fasterxml.jackson.module.kotlin.support

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * This class only support kotlin value class
 */
public object ValueClassSupport {

    /**
     * get boxed value of a **`value class`**.
     * if the receiver is not a `value class`, throw [UnsupportedOperationException]
     */
    public val <T : Any> T.boxedValue: Any?
        @Suppress("UNCHECKED_CAST")
        get() = (this::class as KClass<T>).boxedProperty.get(this)

    /**
     * cache of boxed value property of a **`value class`**.
     */
    private val valueClassFieldCache = mutableMapOf<KClass<out Any>, KProperty1<out Any, *>>()

    /**
     * get boxed value property of a **`value class`**.
     */
    private val <T : Any> KClass<T>.boxedProperty: KProperty1<T, *>
        get() = if (!this.isValue) {
            throw UnsupportedOperationException("$this is not a value class")
        } else {
            @Suppress("UNCHECKED_CAST")
            valueClassFieldCache.getOrPut(this) {
                this.members.first { it is KProperty1<*, *> }.apply { isOpen } as KProperty1<T, *>
            } as KProperty1<T, *>
        }

}

