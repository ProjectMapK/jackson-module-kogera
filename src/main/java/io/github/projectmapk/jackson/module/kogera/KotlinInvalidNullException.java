package io.github.projectmapk.jackson.module.kogera;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.exc.InvalidNullException;
import org.jetbrains.annotations.NotNull;

// Due to a limitation in KT-6653, there is no user-friendly way to override Java getters in Kotlin.
// The reason for not having detailed information(e.g. KParameter) is to keep the class Serializable.
/**
 * Specialized {@link JsonMappingException} sub-class used to indicate that a mandatory Kotlin creator parameter was
 * missing or null.
 */
public final class KotlinInvalidNullException extends InvalidNullException {
    @NotNull
    private final String kotlinPropertyName;

    KotlinInvalidNullException(
            @NotNull
            String kotlinParameterName,
            @NotNull
            Class<?> valueClass,
            @NotNull
            JsonParser p,
            @NotNull
            String msg,
            @NotNull
            PropertyName pname
    ) {
        super(p, msg, pname);
        this.kotlinPropertyName = kotlinParameterName;
        this._targetType = valueClass;
    }

    /**
     * @return Parameter name in Kotlin.
     */
    @NotNull
    public String getKotlinPropertyName() {
        return kotlinPropertyName;
    }

    // region: Override getters to make nullability explicit and to explain its role in this class.
    /**
     * @return Parameter name in Jackson.
     */
    @NotNull
    @Override
    public PropertyName getPropertyName() {
        return super.getPropertyName();
    }

    /**
     * @return The {@link Class} object representing the class that declares the creator.
     */
    @NotNull
    @Override
    public Class<?> getTargetType() {
        return super.getTargetType();
    }
    // endregion
}
