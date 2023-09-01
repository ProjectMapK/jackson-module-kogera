package io.github.projectmapk.jackson.module.kogera

import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer
import com.fasterxml.jackson.databind.util.StdConverter

/**
 * A converter that only performs box processing for the value class.
 * Note that constructor-impl is not called.
 * @param S is nullable because value corresponds to a nullable value class.
 *   see [io.github.projectmapk.jackson.module.kogera.annotation_introspector.KotlinFallbackAnnotationIntrospector.findNullSerializer]
 */
internal class ValueClassBoxConverter<S : Any?, D : Any>(
    unboxedClass: Class<S>,
    val valueClass: Class<D>
) : StdConverter<S, D>() {
    private val boxMethod = valueClass.getDeclaredMethod("box-impl", unboxedClass).apply {
        if (!this.isAccessible) this.isAccessible = true
    }

    @Suppress("UNCHECKED_CAST")
    override fun convert(value: S): D = boxMethod.invoke(null, value) as D

    val delegatingSerializer: StdDelegatingSerializer by lazy { StdDelegatingSerializer(this) }
}
