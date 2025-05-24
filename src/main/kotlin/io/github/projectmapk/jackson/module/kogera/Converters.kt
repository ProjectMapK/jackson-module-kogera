package io.github.projectmapk.jackson.module.kogera

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.databind.util.StdConverter
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.lang.reflect.Method
import java.lang.reflect.Type
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
            INT_CLASS -> IntValueClassBoxConverter(valueClass)
            LONG_CLASS -> LongValueClassBoxConverter(valueClass)
            STRING_CLASS -> StringValueClassBoxConverter(valueClass)
            JAVA_UUID_CLASS -> JavaUuidValueClassBoxConverter(valueClass)
            else -> GenericValueClassBoxConverter(unboxedClass, valueClass)
        }
    }
}

// region: Converters for common classes as wrapped values, add as needed.
internal class IntValueClassBoxConverter<D : Any>(
    override val boxedClass: Class<D>,
) : ValueClassBoxConverter<Int, D>() {
    override val boxHandle: MethodHandle = rawBoxHandle(INT_CLASS).asType(INT_TO_ANY_METHOD_TYPE)

    @Suppress("UNCHECKED_CAST")
    override fun convert(value: Int): D = boxHandle.invokeExact(value) as D
}

internal class LongValueClassBoxConverter<D : Any>(
    override val boxedClass: Class<D>,
) : ValueClassBoxConverter<Long, D>() {
    override val boxHandle: MethodHandle = rawBoxHandle(LONG_CLASS).asType(LONG_TO_ANY_METHOD_TYPE)

    @Suppress("UNCHECKED_CAST")
    override fun convert(value: Long): D = boxHandle.invokeExact(value) as D
}

internal class StringValueClassBoxConverter<D : Any>(
    override val boxedClass: Class<D>,
) : ValueClassBoxConverter<String?, D>() {
    override val boxHandle: MethodHandle = rawBoxHandle(STRING_CLASS).asType(STRING_TO_ANY_METHOD_TYPE)

    @Suppress("UNCHECKED_CAST")
    override fun convert(value: String?): D = boxHandle.invokeExact(value) as D
}

internal class JavaUuidValueClassBoxConverter<D : Any>(
    override val boxedClass: Class<D>,
) : ValueClassBoxConverter<UUID?, D>() {
    override val boxHandle: MethodHandle = rawBoxHandle(JAVA_UUID_CLASS).asType(JAVA_UUID_TO_ANY_METHOD_TYPE)

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

internal sealed class ValueClassUnboxConverter<S : Any, D : Any?> : StdConverter<S, D>() {
    abstract val valueClass: Class<S>
    abstract val unboxedType: Type
    abstract val unboxHandle: MethodHandle

    final override fun getInputType(typeFactory: TypeFactory): JavaType = typeFactory.constructType(valueClass)
    final override fun getOutputType(typeFactory: TypeFactory): JavaType = typeFactory.constructType(unboxedType)

    val delegatingSerializer: StdDelegatingSerializer by lazy { StdDelegatingSerializer(this) }

    companion object {
        fun create(valueClass: Class<*>): ValueClassUnboxConverter<*, *> {
            val unboxMethod = valueClass.getDeclaredMethod("unbox-impl")
            val unboxedType = unboxMethod.genericReturnType

            return when (unboxedType) {
                INT_CLASS -> IntValueClassUnboxConverter(valueClass, unboxMethod)
                LONG_CLASS -> LongValueClassUnboxConverter(valueClass, unboxMethod)
                STRING_CLASS -> StringValueClassUnboxConverter(valueClass, unboxMethod)
                JAVA_UUID_CLASS -> JavaUuidValueClassUnboxConverter(valueClass, unboxMethod)
                else -> GenericValueClassUnboxConverter(valueClass, unboxedType, unboxMethod)
            }
        }
    }
}

internal class IntValueClassUnboxConverter<T : Any>(
    override val valueClass: Class<T>,
    unboxMethod: Method,
) : ValueClassUnboxConverter<T, Int>() {
    override val unboxedType: Type get() = INT_CLASS
    override val unboxHandle: MethodHandle =
        MethodHandles.lookup().unreflect(unboxMethod).asType(ANY_TO_INT_METHOD_TYPE)

    override fun convert(value: T): Int = unboxHandle.invokeExact(value) as Int
}

internal class LongValueClassUnboxConverter<T : Any>(
    override val valueClass: Class<T>,
    unboxMethod: Method,
) : ValueClassUnboxConverter<T, Long>() {
    override val unboxedType: Type get() = LONG_CLASS
    override val unboxHandle: MethodHandle =
        MethodHandles.lookup().unreflect(unboxMethod).asType(ANY_TO_LONG_METHOD_TYPE)

    override fun convert(value: T): Long = unboxHandle.invokeExact(value) as Long
}

internal class StringValueClassUnboxConverter<T : Any>(
    override val valueClass: Class<T>,
    unboxMethod: Method,
) : ValueClassUnboxConverter<T, String?>() {
    override val unboxedType: Type get() = STRING_CLASS
    override val unboxHandle: MethodHandle =
        MethodHandles.lookup().unreflect(unboxMethod).asType(ANY_TO_STRING_METHOD_TYPE)

    override fun convert(value: T): String? = unboxHandle.invokeExact(value) as String?
}

internal class JavaUuidValueClassUnboxConverter<T : Any>(
    override val valueClass: Class<T>,
    unboxMethod: Method,
) : ValueClassUnboxConverter<T, UUID?>() {
    override val unboxedType: Type get() = JAVA_UUID_CLASS
    override val unboxHandle: MethodHandle =
        MethodHandles.lookup().unreflect(unboxMethod).asType(ANY_TO_JAVA_UUID_METHOD_TYPE)

    override fun convert(value: T): UUID? = unboxHandle.invokeExact(value) as UUID?
}

internal class GenericValueClassUnboxConverter<T : Any>(
    override val valueClass: Class<T>,
    override val unboxedType: Type,
    unboxMethod: Method,
) : ValueClassUnboxConverter<T, Any?>() {
    override val unboxHandle: MethodHandle =
        MethodHandles.lookup().unreflect(unboxMethod).asType(ANY_TO_ANY_METHOD_TYPE)

    override fun convert(value: T): Any? = unboxHandle.invokeExact(value)
}
