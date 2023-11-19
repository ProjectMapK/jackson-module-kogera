package io.github.projectmapk.jackson.module.kogera.deser.valueInstantiator;

import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.SoftReference;

// ported from kotlin.reflect.jvm.internal.ReflectProperties 887dc to use LazySoft
class ReflectProperties {
    static abstract class Val<T> {
        private static final Object NULL_VALUE = new Object() {
        };

        @SuppressWarnings({"UnusedParameters", "unused"})
        final T getValue(Object instance, Object metadata) {
            return invoke();
        }

        abstract T invoke();

        protected Object escape(T value) {
            return value == null ? NULL_VALUE : value;
        }

        @SuppressWarnings("unchecked")
        protected T unescape(Object value) {
            return value == NULL_VALUE ? null : (T) value;
        }
    }

    // A delegate for a lazy property on a soft reference, whose initializer may be invoked multiple times
    // including simultaneously from different threads
    static class LazySoftVal<T> extends Val<T> implements Function0<T> {
        private final Function0<T> initializer;
        private volatile SoftReference<Object> value = null;

        LazySoftVal(@Nullable T initialValue, @NotNull Function0<T> initializer) {
            this.initializer = initializer;
            if (initialValue != null) {
                this.value = new SoftReference<Object>(escape(initialValue));
            }
        }

        @Override
        public T invoke() {
            SoftReference<Object> cached = value;
            if (cached != null) {
                Object result = cached.get();
                if (result != null) {
                    return unescape(result);
                }
            }

            T result = initializer.invoke();
            value = new SoftReference<Object>(escape(result));

            return result;
        }
    }

    @NotNull
    static <T> LazySoftVal<T> lazySoft(@Nullable T initialValue, @NotNull Function0<T> initializer) {
        return new LazySoftVal<T>(initialValue, initializer);
    }

    @NotNull
    static <T> LazySoftVal<T> lazySoft(@NotNull Function0<T> initializer) {
        return lazySoft(null, initializer);
    }
}
