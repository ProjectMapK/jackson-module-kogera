package io.github.projectmapk.jackson.module.kogera.deser.deserializers

import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.KeyDeserializer
import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer
import com.fasterxml.jackson.databind.module.SimpleKeyDeserializers
import java.math.BigInteger

// The reason why key is treated as nullable is to match the tentative behavior of StdKeyDeserializer.
// If StdKeyDeserializer is modified, need to modify this too.

internal object UByteKeyDeserializer : StdKeyDeserializer(TYPE_SHORT, UByte::class.java) {
    override fun deserializeKey(key: String?, ctxt: DeserializationContext): UByte? =
        key?.let { UByteChecker.readWithRangeCheck(null, _parseInt(it)) }
}

internal object UShortKeyDeserializer : StdKeyDeserializer(TYPE_INT, UShort::class.java) {
    override fun deserializeKey(key: String?, ctxt: DeserializationContext): UShort? =
        key?.let { UShortChecker.readWithRangeCheck(null, _parseInt(it)) }
}

internal object UIntKeyDeserializer : StdKeyDeserializer(TYPE_LONG, UInt::class.java) {
    override fun deserializeKey(key: String?, ctxt: DeserializationContext): UInt? =
        key?.let { UIntChecker.readWithRangeCheck(null, _parseLong(it)) }
}

internal object ULongKeyDeserializer : StdKeyDeserializer(-1, ULong::class.java) {
    override fun deserializeKey(key: String?, ctxt: DeserializationContext): ULong? =
        key?.let { ULongChecker.readWithRangeCheck(null, BigInteger(it)) }
}

internal object KotlinKeyDeserializers : SimpleKeyDeserializers() {
    override fun findKeyDeserializer(
        type: JavaType,
        config: DeserializationConfig?,
        beanDesc: BeanDescription?
    ): KeyDeserializer? = when (type.rawClass) {
        UByte::class.java -> UByteKeyDeserializer
        UShort::class.java -> UShortKeyDeserializer
        UInt::class.java -> UIntKeyDeserializer
        ULong::class.java -> ULongKeyDeserializer
        else -> null
    }
}
