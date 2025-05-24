package io.github.projectmapk.jackson.module.kogera.ser.serializers

import com.fasterxml.jackson.annotation.JsonKey
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

internal class ValueClassUnboxKeySerializer<T : Any>(
    private val converter: ValueClassUnboxConverter<T, *>,
) : StdSerializer<T>(converter.valueClass) {
    override fun serialize(value: T, gen: JsonGenerator, provider: SerializerProvider) {
        val unboxed = converter.convert(value)

        if (unboxed == null) {
            val javaType = converter.getOutputType(provider.typeFactory)
            provider.findNullKeySerializer(javaType, null).serialize(null, gen, provider)
            return
        }

        provider.findKeySerializer(unboxed::class.java, null).serialize(unboxed, gen, provider)
    }
}

// Class must be UnboxableValueClass.
private fun Class<*>.getStaticJsonKeyGetter(): Method? = this.declaredMethods.find { method ->
    Modifier.isStatic(method.modifiers) && method.annotations.any { it is JsonKey && it.value }
}

internal class ValueClassStaticJsonKeySerializer<T : Any>(
    private val converter: ValueClassUnboxConverter<T, *>,
    private val staticJsonKeyGetter: Method,
) : StdSerializer<T>(converter.valueClass) {
    private val keyType: Class<*> = staticJsonKeyGetter.returnType

    override fun serialize(value: T, gen: JsonGenerator, provider: SerializerProvider) {
        val unboxed = converter.convert(value)
        // As shown in the processing of the factory function, jsonValueGetter is always a static method.
        val jsonKey: Any? = staticJsonKeyGetter.invoke(null, unboxed)

        val serializer = jsonKey
            ?.let { provider.findKeySerializer(keyType, null) }
            ?: provider.findNullKeySerializer(provider.constructType(keyType), null)

        serializer.serialize(jsonKey, gen, provider)
    }

    companion object {
        // `t` must be UnboxableValueClass.
        // If create a function with a JsonValue in the value class,
        // it will be compiled as a static method (= cannot be processed properly by Jackson),
        // so use a ValueClassSerializer.StaticJsonValue to handle this.
        fun <T : Any> createOrNull(converter: ValueClassUnboxConverter<T, *>): StdSerializer<T>? = converter.valueClass
            .getStaticJsonKeyGetter()
            ?.let { ValueClassStaticJsonKeySerializer(converter, it) }
    }
}

internal class KotlinKeySerializers(private val cache: ReflectionCache) : SimpleSerializers() {
    override fun findSerializer(
        config: SerializationConfig,
        type: JavaType,
        beanDesc: BeanDescription,
    ): JsonSerializer<*>? {
        val rawClass = type.rawClass

        return when {
            rawClass.isUnboxableValueClass() -> {
                val unboxConverter = cache.getValueClassUnboxConverter(rawClass)
                ValueClassStaticJsonKeySerializer.createOrNull(unboxConverter)
                    ?: ValueClassUnboxKeySerializer(unboxConverter)
            }
            else -> null
        }
    }
}
