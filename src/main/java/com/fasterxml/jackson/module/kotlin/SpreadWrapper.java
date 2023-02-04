package com.fasterxml.jackson.module.kotlin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

abstract class SpreadWrapper {
    private SpreadWrapper() {}

    @NotNull
    static <T> T newInstance(
            @NotNull Constructor<T> constructor,
            @NotNull Object[] initargs
    ) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return constructor.newInstance(initargs);
    }

    @Nullable
    static Object invoke(
            @NotNull Method method,
            @Nullable Object instance,
            @NotNull Object[] args
    ) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(instance, args);
    }
}
