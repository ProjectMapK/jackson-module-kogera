package io.github.projectmapk.jackson.module.kogera

import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer
import com.fasterxml.jackson.databind.util.StdConverter

// S is nullable because value corresponds to a nullable value class
// @see KotlinFallbackAnnotationIntrospector.findNullSerializer
internal class ValueClassBoxConverter<S : Any?, D : Any>(
    unboxedClass: Class<S>,
    valueClass: Class<D>
) : StdConverter<S, D>() {
    private val boxMethod = valueClass.getDeclaredMethod("box-impl", unboxedClass).apply {
        if (!this.isAccessible) this.isAccessible = true
    }

    @Suppress("UNCHECKED_CAST")
    override fun convert(value: S): D = boxMethod.invoke(null, value) as D

    val delegatingSerializer: StdDelegatingSerializer by lazy { StdDelegatingSerializer(this) }
}
