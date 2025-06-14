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
import io.github.projectmapk.jackson.module.kogera.unreflectAsType
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
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

internal sealed class ValueClassStaticJsonKeySerializer<T : Any>(
    converter: ValueClassUnboxConverter<T, *>,
    staticJsonValueGetter: Method,
    methodType: MethodType,
) : StdSerializer<T>(converter.valueClass) {
    private val keyType: Class<*> = staticJsonValueGetter.returnType
    private val handle: MethodHandle = unreflectAsType(staticJsonValueGetter, methodType).let {
        MethodHandles.filterReturnValue(converter.unboxHandle, it)
    }

    final override fun serialize(value: T, gen: JsonGenerator, provider: SerializerProvider) {
        val jsonKey: Any? = handle.invokeExact(value)

        val serializer = jsonKey
            ?.let { provider.findKeySerializer(keyType, null) }
            ?: provider.findNullKeySerializer(provider.constructType(keyType), null)

        serializer.serialize(jsonKey, gen, provider)
    }

    internal class WrapsInt<T : Any>(
        converter: IntValueClassUnboxConverter<T>,
        staticJsonValueGetter: Method,
    ) : ValueClassStaticJsonKeySerializer<T>(
        converter,
        staticJsonValueGetter,
        INT_TO_ANY_METHOD_TYPE,
    )

    internal class WrapsLong<T : Any>(
        converter: LongValueClassUnboxConverter<T>,
        staticJsonValueGetter: Method,
    ) : ValueClassStaticJsonKeySerializer<T>(
        converter,
        staticJsonValueGetter,
        LONG_TO_ANY_METHOD_TYPE,
    )

    internal class WrapsString<T : Any>(
        converter: StringValueClassUnboxConverter<T>,
        staticJsonValueGetter: Method,
    ) : ValueClassStaticJsonKeySerializer<T>(
        converter,
        staticJsonValueGetter,
        STRING_TO_ANY_METHOD_TYPE,
    )

    internal class WrapsJavaUuid<T : Any>(
        converter: JavaUuidValueClassUnboxConverter<T>,
        staticJsonValueGetter: Method,
    ) : ValueClassStaticJsonKeySerializer<T>(
        converter,
        staticJsonValueGetter,
        JAVA_UUID_TO_ANY_METHOD_TYPE,
    )

    internal class WrapsAny<T : Any>(
        converter: GenericValueClassUnboxConverter<T>,
        staticJsonValueGetter: Method,
    ) : ValueClassStaticJsonKeySerializer<T>(
        converter,
        staticJsonValueGetter,
        ANY_TO_ANY_METHOD_TYPE,

    )

    companion object {
        // Class must be UnboxableValueClass.
        private fun Class<*>.getStaticJsonKeyGetter(): Method? = this.declaredMethods.find { method ->
            Modifier.isStatic(method.modifiers) && method.annotations.any { it is JsonKey && it.value }
        }

        // `t` must be UnboxableValueClass.
        // If create a function with a JsonValue in the value class,
        // it will be compiled as a static method (= cannot be processed properly by Jackson),
        // so use a ValueClassSerializer.StaticJsonValue to handle this.
        fun <T : Any> createOrNull(
            converter: ValueClassUnboxConverter<T, *>,
        ): ValueClassStaticJsonKeySerializer<T>? = converter
            .valueClass
            .getStaticJsonKeyGetter()
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
