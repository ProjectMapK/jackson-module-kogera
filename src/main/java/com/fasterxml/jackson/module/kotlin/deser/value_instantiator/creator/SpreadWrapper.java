package com.fasterxml.jackson.module.kotlin.deser.value_instantiator.creator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class SpreadWrapper {
    static <T> T newInstance(
            @NotNull Constructor<T> constructor,
            @NotNull Object[] initargs
    ) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return constructor.newInstance(initargs);
    }

    public static Object invoke(
            @NotNull Method method,
            @Nullable Object instance,
            @NotNull Object[] args
    ) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(instance, args);
    }
}
