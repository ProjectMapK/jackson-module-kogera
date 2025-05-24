package io.github.projectmapk.jackson.module.kogera

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.databind.util.ClassUtil
import com.fasterxml.jackson.databind.util.StdConverter
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

internal sealed class ValueClassBoxConverter<S : Any?, D : Any> : StdConverter<S, D>() {
    abstract val boxedClass: Class<D>
    abstract val boxHandle: MethodHandle

    protected fun rawBoxHandle(
        unboxedClass: Class<*>,
    ): MethodHandle = MethodHandles.lookup().findStatic(
        boxedClass,
        "box-impl",
        MethodType.methodType(boxedClass, unboxedClass),
    )

    val delegatingSerializer: StdDelegatingSerializer by lazy { StdDelegatingSerializer(this) }
}

internal class IntValueClassBoxConverter<D : Any>(
    override val boxedClass: Class<D>,
) : ValueClassBoxConverter<Int, D>() {
    override val boxHandle: MethodHandle = rawBoxHandle(Int::class.java).asType(MethodType.methodType(ANY_CLASS, Int::class.java))

    @Suppress("UNCHECKED_CAST")
    override fun convert(value: Int): D = boxHandle.invokeExact(value) as D
}

/**
 * A converter that only performs box processing for the value class.
 * Note that constructor-impl is not called.
 * @param S is nullable because value corresponds to a nullable value class.
 *   see [io.github.projectmapk.jackson.module.kogera.annotationIntrospector.KotlinFallbackAnnotationIntrospector.findNullSerializer]
 */
internal class GenericValueClassBoxConverter<S : Any?, D : Any>(
    unboxedClass: Class<S>,
    override val boxedClass: Class<D>,
) : ValueClassBoxConverter<S, D>() {
    override val boxHandle: MethodHandle = rawBoxHandle(unboxedClass).asType(ANY_TO_ANY_METHOD_TYPE)

    @Suppress("UNCHECKED_CAST")
    override fun convert(value: S): D = boxHandle.invokeExact(value) as D
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
