package io.github.projectmapk.jackson.module.kogera;

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

    @NotNull
    static <T> Constructor<T> getDeclaredConstructor(
            @NotNull Class<T> clazz,
            @NotNull Class<?>[] parameterTypes
    ) throws NoSuchMethodException {
        return clazz.getDeclaredConstructor(parameterTypes);
    }

    @NotNull
    static Method getDeclaredMethod(
            @NotNull Class<?> clazz,
            @NotNull String name,
            @NotNull Class<?>[] parameterTypes
    ) throws NoSuchMethodException {
        return clazz.getDeclaredMethod(name, parameterTypes);
    }
}
