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
import io.github.projectmapk.jackson.module.kogera.ReflectionCache
import io.github.projectmapk.jackson.module.kogera.ValueClassUnboxConverter
import io.github.projectmapk.jackson.module.kogera.isUnboxableValueClass
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

internal class ValueClassStaticJsonValueSerializer<T : Any>(
    private val converter: ValueClassUnboxConverter<T>,
    private val staticJsonValueGetter: Method
) : StdSerializer<T>(converter.valueClass) {
    override fun serialize(value: T, gen: JsonGenerator, provider: SerializerProvider) {
        val unboxed = converter.convert(value)
        // As shown in the processing of the factory function, jsonValueGetter is always a static method.
        val jsonValue: Any? = staticJsonValueGetter.invoke(null, unboxed)
        provider.defaultSerializeValue(jsonValue, gen)
    }

    companion object {
        // `t` must be UnboxableValueClass.
        // If create a function with a JsonValue in the value class,
        // it will be compiled as a static method (= cannot be processed properly by Jackson),
        // so use a ValueClassSerializer.StaticJsonValue to handle this.
        fun <T : Any> createOrNull(converter: ValueClassUnboxConverter<T>): StdSerializer<T>? = converter
            .valueClass
            .getStaticJsonValueGetter()
            ?.let { ValueClassStaticJsonValueSerializer(converter, it) }
    }
}

internal class KotlinSerializers(private val cache: ReflectionCache) : SimpleSerializers() {
    override fun findSerializer(
        config: SerializationConfig?,
        type: JavaType,
        beanDesc: BeanDescription?
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
