package io.github.projectmapk.jackson.module.kogera.ser.serializers

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleSerializers
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import io.github.projectmapk.jackson.module.kogera.ANY_TO_ANY_METHOD_TYPE
import io.github.projectmapk.jackson.module.kogera.GenericValueClassUnboxConverter
import io.github.projectmapk.jackson.module.kogera.INT_TO_ANY_METHOD_TYPE
import io.github.projectmapk.jackson.module.kogera.IntValueClassUnboxConverter
import io.github.projectmapk.jackson.module.kogera.JAVA_UUID_TO_ANY_METHOD_TYPE
import io.github.projectmapk.jackson.module.kogera.JavaUuidValueClassUnboxConverter
import io.github.projectmapk.jackson.module.kogera.LONG_TO_ANY_METHOD_TYPE
import io.github.projectmapk.jackson.module.kogera.LongValueClassUnboxConverter
import io.github.projectmapk.jackson.module.kogera.ReflectionCache
import io.github.projectmapk.jackson.module.kogera.STRING_TO_ANY_METHOD_TYPE
import io.github.projectmapk.jackson.module.kogera.StringValueClassUnboxConverter
import io.github.projectmapk.jackson.module.kogera.ValueClassUnboxConverter
import io.github.projectmapk.jackson.module.kogera.isUnboxableValueClass
import io.github.projectmapk.jackson.module.kogera.unreflectAsTypeWithAccessibilityModification
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.math.BigInteger

internal object UByteSerializer : StdSerializer<UByte>(UByte::class.java) {
    private fun readResolve(): Any = UByteSerializer

    override fun serialize(value: UByte, gen: JsonGenerator, provider: SerializerProvider) = gen
        .writeNumber(value.toShort())
}

internal object UShortSerializer : StdSerializer<UShort>(UShort::class.java) {
    private fun readResolve(): Any = UShortSerializer

    override fun serialize(value: UShort, gen: JsonGenerator, provider: SerializerProvider) = gen
        .writeNumber(value.toInt())
}

internal object UIntSerializer : StdSerializer<UInt>(UInt::class.java) {
    private fun readResolve(): Any = UIntSerializer

    override fun serialize(value: UInt, gen: JsonGenerator, provider: SerializerProvider) = gen
        .writeNumber(value.toLong())
}

internal object ULongSerializer : StdSerializer<ULong>(ULong::class.java) {
    private fun readResolve(): Any = ULongSerializer

    override fun serialize(value: ULong, gen: JsonGenerator, provider: SerializerProvider) {
        val longValue = value.toLong()
        when {
            longValue >= 0 -> gen.writeNumber(longValue)
            else -> gen.writeNumber(BigInteger(value.toString()))
        }
    }
}

// Class must be UnboxableValueClass.
private fun Class<*>.getStaticJsonValueGetter(): Method? = this.declaredMethods.find { method ->
    Modifier.isStatic(method.modifiers) && method.annotations.any { it is JsonValue && it.value }
}

internal sealed class ValueClassStaticJsonValueSerializer<T : Any>(
    converter: ValueClassUnboxConverter<T, *>,
    staticJsonValueHandle: MethodHandle,
) : StdSerializer<T>(converter.valueClass) {
    private val handle: MethodHandle = MethodHandles.filterReturnValue(converter.unboxHandle, staticJsonValueHandle)

    final override fun serialize(value: T, gen: JsonGenerator, provider: SerializerProvider) {
        val jsonValue: Any? = handle.invokeExact(value)
        provider.defaultSerializeValue(jsonValue, gen)
    }

    internal class WrapsInt<T : Any>(
        converter: IntValueClassUnboxConverter<T>,
        staticJsonValueGetter: Method,
    ) : ValueClassStaticJsonValueSerializer<T>(
        converter,
        unreflectAsTypeWithAccessibilityModification(staticJsonValueGetter, INT_TO_ANY_METHOD_TYPE),
    )

    internal class WrapsLong<T : Any>(
        converter: LongValueClassUnboxConverter<T>,
        staticJsonValueGetter: Method,
    ) : ValueClassStaticJsonValueSerializer<T>(
        converter,
        unreflectAsTypeWithAccessibilityModification(staticJsonValueGetter, LONG_TO_ANY_METHOD_TYPE),
    )

    internal class WrapsString<T : Any>(
        converter: StringValueClassUnboxConverter<T>,
        staticJsonValueGetter: Method,
    ) : ValueClassStaticJsonValueSerializer<T>(
        converter,
        unreflectAsTypeWithAccessibilityModification(staticJsonValueGetter, STRING_TO_ANY_METHOD_TYPE),
    )

    internal class WrapsJavaUuid<T : Any>(
        converter: JavaUuidValueClassUnboxConverter<T>,
        staticJsonValueGetter: Method,
    ) : ValueClassStaticJsonValueSerializer<T>(
        converter,
        unreflectAsTypeWithAccessibilityModification(staticJsonValueGetter, JAVA_UUID_TO_ANY_METHOD_TYPE),
    )

    internal class WrapsAny<T : Any>(
        converter: GenericValueClassUnboxConverter<T>,
        staticJsonValueGetter: Method,
    ) : ValueClassStaticJsonValueSerializer<T>(
        converter,
        unreflectAsTypeWithAccessibilityModification(staticJsonValueGetter, ANY_TO_ANY_METHOD_TYPE),
    )

    companion object {
        // `t` must be UnboxableValueClass.
        // If create a function with a JsonValue in the value class,
        // it will be compiled as a static method (= cannot be processed properly by Jackson),
        // so use a ValueClassSerializer.StaticJsonValue to handle this.
        fun <T : Any> createOrNull(
            converter: ValueClassUnboxConverter<T, *>,
        ): ValueClassStaticJsonValueSerializer<T>? = converter
            .valueClass
            .getStaticJsonValueGetter()
            ?.let {
                when (converter) {
                    is IntValueClassUnboxConverter -> WrapsInt(converter, it)
                    is LongValueClassUnboxConverter -> WrapsLong(converter, it)
                    is StringValueClassUnboxConverter -> WrapsString(converter, it)
                    is JavaUuidValueClassUnboxConverter -> WrapsJavaUuid(converter, it)
                    is GenericValueClassUnboxConverter -> WrapsAny(converter, it)
                }
            }
    }
}

internal class KotlinSerializers(private val cache: ReflectionCache) : SimpleSerializers() {
    override fun findSerializer(
        config: SerializationConfig?,
        type: JavaType,
        beanDesc: BeanDescription?,
    ): JsonSerializer<*>? {
        val rawClass = type.rawClass

        return when {
            UByte::class.java == rawClass -> UByteSerializer
            UShort::class.java == rawClass -> UShortSerializer
            UInt::class.java == rawClass -> UIntSerializer
            ULong::class.java == rawClass -> ULongSerializer
            // The priority of Unboxing needs to be lowered so as not to break the serialization of Unsigned Integers.
            rawClass.isUnboxableValueClass() -> {
                val unboxConverter = cache.getValueClassUnboxConverter(rawClass)
                ValueClassStaticJsonValueSerializer.createOrNull(unboxConverter) ?: unboxConverter.delegatingSerializer
            }
            else -> null
        }
    }
}
