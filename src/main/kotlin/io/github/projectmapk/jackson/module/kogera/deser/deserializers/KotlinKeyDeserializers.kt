package io.github.projectmapk.jackson.module.kogera.deser.deserializers

import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.KeyDeserializer
import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException
import com.fasterxml.jackson.databind.module.SimpleKeyDeserializers
import io.github.projectmapk.jackson.module.kogera.ANY_TO_ANY_METHOD_TYPE
import io.github.projectmapk.jackson.module.kogera.GenericValueClassBoxConverter
import io.github.projectmapk.jackson.module.kogera.INT_CLASS
import io.github.projectmapk.jackson.module.kogera.IntValueClassBoxConverter
import io.github.projectmapk.jackson.module.kogera.JAVA_UUID_CLASS
import io.github.projectmapk.jackson.module.kogera.JavaUuidValueClassBoxConverter
import io.github.projectmapk.jackson.module.kogera.LONG_CLASS
import io.github.projectmapk.jackson.module.kogera.LongValueClassBoxConverter
import io.github.projectmapk.jackson.module.kogera.ReflectionCache
import io.github.projectmapk.jackson.module.kogera.STRING_CLASS
import io.github.projectmapk.jackson.module.kogera.StringValueClassBoxConverter
import io.github.projectmapk.jackson.module.kogera.ValueClassBoxConverter
import io.github.projectmapk.jackson.module.kogera.isUnboxableValueClass
import io.github.projectmapk.jackson.module.kogera.toSignature
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.reflect.Method
import java.math.BigInteger
import java.util.UUID

// The reason why key is treated as nullable is to match the tentative behavior of StdKeyDeserializer.
// If StdKeyDeserializer is modified, need to modify this too.

internal object UByteKeyDeserializer : StdKeyDeserializer(TYPE_SHORT, UByte::class.java) {
    private fun readResolve(): Any = UByteKeyDeserializer

    override fun deserializeKey(key: String?, ctxt: DeserializationContext): UByte? = key
        ?.let { UByteChecker.readWithRangeCheck(null, _parseInt(it)) }
}

internal object UShortKeyDeserializer : StdKeyDeserializer(TYPE_INT, UShort::class.java) {
    private fun readResolve(): Any = UShortKeyDeserializer

    override fun deserializeKey(key: String?, ctxt: DeserializationContext): UShort? = key
        ?.let { UShortChecker.readWithRangeCheck(null, _parseInt(it)) }
}

internal object UIntKeyDeserializer : StdKeyDeserializer(TYPE_LONG, UInt::class.java) {
    private fun readResolve(): Any = UIntKeyDeserializer

    override fun deserializeKey(key: String?, ctxt: DeserializationContext): UInt? = key
        ?.let { UIntChecker.readWithRangeCheck(null, _parseLong(it)) }
}

internal object ULongKeyDeserializer : StdKeyDeserializer(-1, ULong::class.java) {
    private fun readResolve(): Any = ULongKeyDeserializer

    override fun deserializeKey(key: String?, ctxt: DeserializationContext): ULong? = key
        ?.let { ULongChecker.readWithRangeCheck(null, BigInteger(it)) }
}

// The implementation is designed to be compatible with various creators, just in case.
internal sealed class ValueClassKeyDeserializer<S, D : Any>(
    converter: ValueClassBoxConverter<S, D>,
    creatorHandle: MethodHandle,
) : KeyDeserializer() {
    private val boxedClass: Class<D> = converter.boxedClass

    protected abstract val unboxedClass: Class<*>
    protected val handle: MethodHandle = MethodHandles.filterReturnValue(creatorHandle, converter.boxHandle)

    // Based on databind error
    // https://github.com/FasterXML/jackson-databind/blob/341f8d360a5f10b5e609d6ee0ea023bf597ce98a/src/main/java/com/fasterxml/jackson/databind/deser/DeserializerCache.java#L624
    private fun errorMessage(boxedType: JavaType): String = "Could not find (Map) Key deserializer for types " +
        "wrapped in $boxedType"

    // Since the input to handle must be strict, invoke should be implemented in each class
    protected abstract fun invokeExact(value: S): D

    final override fun deserializeKey(key: String?, ctxt: DeserializationContext): Any {
        val unboxedJavaType = ctxt.constructType(unboxedClass)

        return try {
            // findKeyDeserializer does not return null, and an exception will be thrown if not found.
            val value = ctxt.findKeyDeserializer(unboxedJavaType, null).deserializeKey(key, ctxt)
            @Suppress("UNCHECKED_CAST")
            invokeExact(value as S)
        } catch (e: InvalidDefinitionException) {
            throw JsonMappingException.from(ctxt, errorMessage(ctxt.constructType(boxedClass)), e)
        }
    }

    internal sealed class WrapsSpecified<S, D : Any>(
        converter: ValueClassBoxConverter<S, D>,
        creator: Method,
    ) : ValueClassKeyDeserializer<S, D>(
        converter,
        // Currently, only the primary constructor can be the creator of a key, so for specified types,
        // the return type of the primary constructor and the input type of the box function are exactly the same.
        // Therefore, performance is improved by omitting the asType call.
        MethodHandles.lookup().unreflect(creator),
    )

    internal class WrapsInt<D : Any>(
        converter: IntValueClassBoxConverter<D>,
        creator: Method,
    ) : WrapsSpecified<Int, D>(converter, creator) {
        override val unboxedClass: Class<*> get() = INT_CLASS

        @Suppress("UNCHECKED_CAST")
        override fun invokeExact(value: Int): D = handle.invokeExact(value) as D
    }

    internal class WrapsLong<D : Any>(
        converter: LongValueClassBoxConverter<D>,
        creator: Method,
    ) : WrapsSpecified<Long, D>(converter, creator) {
        override val unboxedClass: Class<*> get() = LONG_CLASS

        @Suppress("UNCHECKED_CAST")
        override fun invokeExact(value: Long): D = handle.invokeExact(value) as D
    }

    internal class WrapsString<D : Any>(
        converter: StringValueClassBoxConverter<D>,
        creator: Method,
    ) : WrapsSpecified<String?, D>(converter, creator) {
        override val unboxedClass: Class<*> get() = STRING_CLASS

        @Suppress("UNCHECKED_CAST")
        override fun invokeExact(value: String?): D = handle.invokeExact(value) as D
    }

    internal class WrapsJavaUuid<D : Any>(
        converter: JavaUuidValueClassBoxConverter<D>,
        creator: Method,
    ) : WrapsSpecified<UUID?, D>(converter, creator) {
        override val unboxedClass: Class<*> get() = JAVA_UUID_CLASS

        @Suppress("UNCHECKED_CAST")
        override fun invokeExact(value: UUID?): D = handle.invokeExact(value) as D
    }

    internal class WrapsAny<S, D : Any>(
        converter: GenericValueClassBoxConverter<S, D>,
        creator: Method,
    ) : ValueClassKeyDeserializer<S, D>(
        converter,
        MethodHandles.lookup().unreflect(creator).asType(ANY_TO_ANY_METHOD_TYPE),
    ) {
        override val unboxedClass: Class<*> = creator.returnType

        @Suppress("UNCHECKED_CAST")
        override fun invokeExact(value: S): D = handle.invokeExact(value) as D
    }

    companion object {
        fun createOrNull(
            valueClass: Class<*>,
            cache: ReflectionCache,
        ): ValueClassKeyDeserializer<*, *>? {
            val jmClass = cache.getJmClass(valueClass) ?: return null
            val primaryKmConstructorSignature =
                jmClass.constructors.first { !it.isSecondary }.signature

            // Only primary constructor is allowed as creator, regardless of visibility.
            // This is because it is based on the WrapsNullableValueClassBoxDeserializer.
            // Also, as far as I could research, there was no such functionality as JsonKeyCreator,
            // so it was not taken into account.
            return valueClass.declaredMethods.find { it.toSignature() == primaryKmConstructorSignature }?.let {
                val unboxedClass = it.returnType

                val converter = cache.getValueClassBoxConverter(unboxedClass, valueClass)

                when (converter) {
                    is IntValueClassBoxConverter -> WrapsInt(converter, it)
                    is LongValueClassBoxConverter -> WrapsLong(converter, it)
                    is StringValueClassBoxConverter -> WrapsString(converter, it)
                    is JavaUuidValueClassBoxConverter -> WrapsJavaUuid(converter, it)
                    is GenericValueClassBoxConverter -> WrapsAny(converter, it)
                }
            }
        }
    }
}

internal class KotlinKeyDeserializers(private val cache: ReflectionCache) : SimpleKeyDeserializers() {
    override fun findKeyDeserializer(
        type: JavaType,
        config: DeserializationConfig?,
        beanDesc: BeanDescription?,
    ): KeyDeserializer? {
        val rawClass = type.rawClass

        return when {
            rawClass == UByte::class.java -> UByteKeyDeserializer
            rawClass == UShort::class.java -> UShortKeyDeserializer
            rawClass == UInt::class.java -> UIntKeyDeserializer
            rawClass == ULong::class.java -> ULongKeyDeserializer
            rawClass.isUnboxableValueClass() -> ValueClassKeyDeserializer.createOrNull(rawClass, cache)
            else -> null
        }
    }
}
