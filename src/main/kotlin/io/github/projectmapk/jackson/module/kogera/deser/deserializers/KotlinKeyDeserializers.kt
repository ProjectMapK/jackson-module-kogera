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
import com.fasterxml.jackson.databind.util.ClassUtil
import io.github.projectmapk.jackson.module.kogera.ReflectionCache
import io.github.projectmapk.jackson.module.kogera.ValueClassBoxConverter
import io.github.projectmapk.jackson.module.kogera.isUnboxableValueClass
import io.github.projectmapk.jackson.module.kogera.toSignature
import java.lang.reflect.Method
import java.math.BigInteger
import kotlin.metadata.isSecondary
import kotlin.metadata.jvm.signature

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
internal class ValueClassKeyDeserializer<S, D : Any>(
    private val creator: Method,
    private val converter: ValueClassBoxConverter<S, D>
) : KeyDeserializer() {
    private val unboxedClass: Class<*> = creator.parameterTypes[0]

    init {
        creator.apply { ClassUtil.checkAndFixAccess(this, false) }
    }

    // Based on databind error
    // https://github.com/FasterXML/jackson-databind/blob/341f8d360a5f10b5e609d6ee0ea023bf597ce98a/src/main/java/com/fasterxml/jackson/databind/deser/DeserializerCache.java#L624
    private fun errorMessage(boxedType: JavaType): String = "Could not find (Map) Key deserializer for types " +
        "wrapped in $boxedType"

    override fun deserializeKey(key: String?, ctxt: DeserializationContext): Any {
        val unboxedJavaType = ctxt.constructType(unboxedClass)

        return try {
            // findKeyDeserializer does not return null, and an exception will be thrown if not found.
            val value = ctxt.findKeyDeserializer(unboxedJavaType, null).deserializeKey(key, ctxt)
            @Suppress("UNCHECKED_CAST")
            converter.convert(creator.invoke(null, value) as S)
        } catch (e: InvalidDefinitionException) {
            throw JsonMappingException.from(ctxt, errorMessage(ctxt.constructType(converter.boxedClass)), e)
        }
    }

    companion object {
        fun createOrNull(
            valueClass: Class<*>,
            cache: ReflectionCache
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

                ValueClassKeyDeserializer(it, converter)
            }
        }
    }
}

internal class KotlinKeyDeserializers(private val cache: ReflectionCache) : SimpleKeyDeserializers() {
    override fun findKeyDeserializer(
        type: JavaType,
        config: DeserializationConfig?,
        beanDesc: BeanDescription?
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
