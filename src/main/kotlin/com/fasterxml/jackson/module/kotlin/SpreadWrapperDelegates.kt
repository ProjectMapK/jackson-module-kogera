package com.fasterxml.jackson.module.kotlin

import java.lang.reflect.Constructor
import java.lang.reflect.Method

internal fun <T> Constructor<T>.call(args: Array<*>): T = SpreadWrapper.newInstance(this, args)
internal fun Method.call(instance: Any?, args: Array<*>): Any? = SpreadWrapper.invoke(this, instance, args)
