package io.github.projectmapk.jackson.module.kogera

import java.lang.reflect.Constructor
import java.lang.reflect.Method

internal fun <T> Constructor<T>.call(args: Array<*>): T = SpreadWrapper.newInstance(this, args)
internal fun Method.call(instance: Any?, args: Array<*>): Any? = SpreadWrapper.invoke(this, instance, args)

internal fun <T> Class<T>.getDeclaredConstructorBy(parameterTypes: Array<Class<*>>): Constructor<T> = SpreadWrapper
    .getDeclaredConstructor(this, parameterTypes)
internal fun Class<*>.getDeclaredMethodBy(name: String, parameterTypes: Array<Class<*>>): Method = SpreadWrapper
    .getDeclaredMethod(this, name, parameterTypes)
