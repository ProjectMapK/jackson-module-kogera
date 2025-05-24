package io.github.projectmapk.jackson.module.kogera

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.databind.util.ClassUtil
import com.fasterxml.jackson.databind.util.StdConverter
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

/**
 * A converter that only performs box processing for the value class.
 * Note that constructor-impl is not called.
 * @param S is nullable because value corresponds to a nullable value class.
 *   see [io.github.projectmapk.jackson.module.kogera.annotationIntrospector.KotlinFallbackAnnotationIntrospector.findNullSerializer]
 */
internal class ValueClassBoxConverter<S : Any?, D : Any>(
    unboxedClass: Class<S>,
    val boxedClass: Class<D>,
) : StdConverter<S, D>() {
    val boxHandle: MethodHandle = MethodHandles.lookup().findStatic(
        boxedClass,
        "box-impl",
        MethodType.methodType(boxedClass, unboxedClass),
    ).asType(ANY_TO_ANY_METHOD_TYPE)

    @Suppress("UNCHECKED_CAST")
    override fun convert(value: S): D = boxHandle.invokeExact(value) as D

    val delegatingSerializer: StdDelegatingSerializer by lazy { StdDelegatingSerializer(this) }
}

internal class ValueClassUnboxConverter<T : Any>(val valueClass: Class<T>) : StdConverter<T, Any?>() {
    private val unboxMethod = valueClass.getDeclaredMethod("unbox-impl").apply {
        ClassUtil.checkAndFixAccess(this, false)
    }

    override fun convert(value: T): Any? = unboxMethod.invoke(value)

    override fun getInputType(typeFactory: TypeFactory): JavaType = typeFactory.constructType(valueClass)
    override fun getOutputType(
        typeFactory: TypeFactory,
    ): JavaType = typeFactory.constructType(unboxMethod.genericReturnType)

    val delegatingSerializer: StdDelegatingSerializer by lazy { StdDelegatingSerializer(this) }
}
