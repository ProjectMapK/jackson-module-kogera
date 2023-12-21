package io.github.projectmapk.jackson.module.kogera

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.databind.util.StdConverter

/**
 * A converter that only performs box processing for the value class.
 * Note that constructor-impl is not called.
 * @param S is nullable because value corresponds to a nullable value class.
 *   see [io.github.projectmapk.jackson.module.kogera.annotationIntrospector.KotlinFallbackAnnotationIntrospector.findNullSerializer]
 */
internal class ValueClassBoxConverter<S : Any?, D : Any>(
    unboxedClass: Class<S>,
    val boxedClass: Class<D>
) : StdConverter<S, D>() {
    private val boxMethod = boxedClass.getDeclaredMethod("box-impl", unboxedClass).apply {
        if (!this.isAccessible) this.isAccessible = true
    }

    @Suppress("UNCHECKED_CAST")
    override fun convert(value: S): D = boxMethod.invoke(null, value) as D

    val delegatingSerializer: StdDelegatingSerializer by lazy { StdDelegatingSerializer(this) }
}

internal class ValueClassUnboxConverter<T : Any>(val valueClass: Class<T>) : StdConverter<T, Any?>() {
    private val unboxMethod = valueClass.getDeclaredMethod("unbox-impl").apply {
        if (!this.isAccessible) this.isAccessible = true
    }
    val unboxedClass: Class<*> = unboxMethod.returnType

    override fun convert(value: T): Any? = unboxMethod.invoke(value)

    override fun getInputType(typeFactory: TypeFactory): JavaType = typeFactory.constructType(valueClass)
    override fun getOutputType(typeFactory: TypeFactory): JavaType =
        typeFactory.constructType(unboxMethod.genericReturnType)

    val delegatingSerializer: StdDelegatingSerializer by lazy { StdDelegatingSerializer(this) }
}
