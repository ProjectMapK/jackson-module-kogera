package io.github.projectmapk.jackson.module.kogera.deser;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.projectmapk.jackson.module.kogera.deser.deserializers.ValueClassBoxDeserializer;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An interface to be inherited by JsonDeserializer that handles value classes that may wrap nullable.
 * @see ValueClassBoxDeserializer for implementation.
 */
// To ensure maximum compatibility with StdDeserializer, this class is defined in Java.
public abstract class ValueClassDeserializer<D> extends StdDeserializer<D> {
    protected ValueClassDeserializer(@NotNull KClass<?> vc) {
        super(JvmClassMappingKt.getJavaClass(vc));
    }

    protected ValueClassDeserializer(@NotNull Class<?> vc) {
        super(vc);
    }

    protected ValueClassDeserializer(@NotNull JavaType valueType) {
        super(valueType);
    }

    protected ValueClassDeserializer(@NotNull StdDeserializer<D> src) {
        super(src);
    }

    @Override
    @NotNull
    public final Class<D> handledType() {
        //noinspection unchecked
        return (Class<D>) super.handledType();
    }

    /**
     * If the parameter definition is a value class that wraps a nullable and is non-null,
     * and the input to JSON is explicitly null, this value is used.
     */
    // It is defined so that null can also be returned so that Nulls.SKIP can be applied.
    @Nullable
    public abstract D getBoxedNullValue();
}
