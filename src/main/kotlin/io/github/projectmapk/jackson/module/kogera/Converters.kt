package io.github.projectmapk.jackson.module.kogera

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.databind.util.ClassUtil
import com.fasterxml.jackson.databind.util.StdConverter
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.UUID

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

    companion object {
        fun create(
            unboxedClass: Class<*>,
            valueClass: Class<*>,
        ): ValueClassBoxConverter<*, *> = when (unboxedClass) {
            Int::class.java -> IntValueClassBoxConverter(valueClass)
            Long::class.java -> LongValueClassBoxConverter(valueClass)
            String::class.java -> StringValueClassBoxConverter(valueClass)
            UUID::class.java -> JavaUuidValueClassBoxConverter(valueClass)
            else -> GenericValueClassBoxConverter(unboxedClass, valueClass)
        }
    }
}

// region: Converters for common classes as wrapped values, add as needed.
internal class IntValueClassBoxConverter<D : Any>(
    override val boxedClass: Class<D>,
) : ValueClassBoxConverter<Int, D>() {
    override val boxHandle: MethodHandle = rawBoxHandle(Int::class.java).asType(MethodType.methodType(ANY_CLASS, Int::class.java))

    @Suppress("UNCHECKED_CAST")
    override fun convert(value: Int): D = boxHandle.invokeExact(value) as D
}

internal class LongValueClassBoxConverter<D : Any>(
    override val boxedClass: Class<D>,
) : ValueClassBoxConverter<Long, D>() {
    override val boxHandle: MethodHandle = rawBoxHandle(Long::class.java).asType(MethodType.methodType(ANY_CLASS, Long::class.java))

    @Suppress("UNCHECKED_CAST")
    override fun convert(value: Long): D = boxHandle.invokeExact(value) as D
}

internal class StringValueClassBoxConverter<D : Any>(
    override val boxedClass: Class<D>,
) : ValueClassBoxConverter<String?, D>() {
    override val boxHandle: MethodHandle = rawBoxHandle(String::class.java).asType(MethodType.methodType(ANY_CLASS, String::class.java))

    @Suppress("UNCHECKED_CAST")
    override fun convert(value: String?): D = boxHandle.invokeExact(value) as D
}

internal class JavaUuidValueClassBoxConverter<D : Any>(
    override val boxedClass: Class<D>,
) : ValueClassBoxConverter<UUID?, D>() {
    override val boxHandle: MethodHandle = rawBoxHandle(UUID::class.java).asType(MethodType.methodType(ANY_CLASS, UUID::class.java))

    @Suppress("UNCHECKED_CAST")
    override fun convert(value: UUID?): D = boxHandle.invokeExact(value) as D
}
// endregion

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
