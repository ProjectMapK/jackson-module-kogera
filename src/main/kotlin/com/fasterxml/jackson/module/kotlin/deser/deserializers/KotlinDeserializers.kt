package com.fasterxml.jackson.module.kotlin.deser.deserializers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.Deserializers
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

internal object SequenceDeserializer : StdDeserializer<Sequence<*>>(Sequence::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Sequence<*> {
        return ctxt.readValue(p, List::class.java).asSequence()
    }
}

internal object RegexDeserializer : StdDeserializer<Regex>(Regex::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Regex {
        val node = ctxt.readTree(p)

        if (node.isTextual) {
            return Regex(node.asText())
        } else if (node.isObject) {
            val pattern = node.get("pattern").asText()
            val options = if (node.has("options")) {
                val optionsNode = node.get("options")
                if (!optionsNode.isArray) {
                    throw IllegalStateException(
                        "Expected an array of strings for RegexOptions, but type was ${node.nodeType}"
                    )
                }
                optionsNode.elements().asSequence().map { RegexOption.valueOf(it.asText()) }.toSet()
            } else {
                emptySet()
            }
            return Regex(pattern, options)
        } else {
            throw IllegalStateException(
                "Expected a string or an object to deserialize a Regex, but type was ${node.nodeType}"
            )
        }
    }
}

internal object UByteDeserializer : StdDeserializer<UByte>(UByte::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext) =
        UByteChecker.readWithRangeCheck(p, p.intValue)
}

internal object UShortDeserializer : StdDeserializer<UShort>(UShort::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext) =
        UShortChecker.readWithRangeCheck(p, p.intValue)
}

internal object UIntDeserializer : StdDeserializer<UInt>(UInt::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext) =
        UIntChecker.readWithRangeCheck(p, p.longValue)
}

internal object ULongDeserializer : StdDeserializer<ULong>(ULong::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext) =
        ULongChecker.readWithRangeCheck(p, p.bigIntegerValue)
}

internal class KotlinDeserializers : Deserializers.Base() {
    override fun findBeanDeserializer(
        type: JavaType,
        config: DeserializationConfig?,
        beanDesc: BeanDescription?
    ): JsonDeserializer<*>? {
        return when (type.rawClass) {
            Sequence::class.java -> SequenceDeserializer
            Regex::class.java -> RegexDeserializer
            UByte::class.java -> UByteDeserializer
            UShort::class.java -> UShortDeserializer
            UInt::class.java -> UIntDeserializer
            ULong::class.java -> ULongDeserializer
            else -> null
        }
    }
}
