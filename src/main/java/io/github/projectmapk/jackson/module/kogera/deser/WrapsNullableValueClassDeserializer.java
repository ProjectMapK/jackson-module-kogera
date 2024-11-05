package io.github.projectmapk.jackson.module.kogera.deser;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.projectmapk.jackson.module.kogera.deser.deserializers.WrapsNullableValueClassBoxDeserializer;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

/**
 * An interface to be inherited by JsonDeserializer that handles value classes that may wrap nullable.
 * @see WrapsNullableValueClassBoxDeserializer for implementation.
 */
// To ensure maximum compatibility with StdDeserializer, this class is written in Java.
public abstract class WrapsNullableValueClassDeserializer<D> extends StdDeserializer<D> {
    protected WrapsNullableValueClassDeserializer(@NotNull KClass<?> vc) {
        super(JvmClassMappingKt.getJavaClass(vc));
    }

    protected WrapsNullableValueClassDeserializer(@NotNull Class<?> vc) {
        super(vc);
    }

    protected WrapsNullableValueClassDeserializer(@NotNull JavaType valueType) {
        super(valueType);
    }

    protected WrapsNullableValueClassDeserializer(@NotNull StdDeserializer<D> src) {
        super(src);
    }

    @Override
    @NotNull
    public final Class<?> handledType() {
        return Objects.requireNonNull(super.handledType());
    }

    /**
     * If the parameter definition is a value class that wraps a nullable and is non-null,
     * and the input to JSON is explicitly null, this value is used.
     */
    // It is defined so that null can also be returned so that Nulls.SKIP can be applied.
    @Nullable
    public abstract D getBoxedNullValue();

    @Override
    public abstract D deserialize(@NotNull JsonParser p, @NotNull DeserializationContext ctxt)
            throws IOException, JacksonException;
}
