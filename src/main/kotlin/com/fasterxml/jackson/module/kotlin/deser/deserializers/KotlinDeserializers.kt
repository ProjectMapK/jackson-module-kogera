package com.fasterxml.jackson.module.kotlin.deser.deserializers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.Deserializers
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.module.kotlin.ReflectionCache
import com.fasterxml.jackson.module.kotlin.hasCreatorAnnotation
import com.fasterxml.jackson.module.kotlin.isUnboxableValueClass
import com.fasterxml.jackson.module.kotlin.toSignature
import kotlinx.metadata.Flag
import kotlinx.metadata.KmClass
import kotlinx.metadata.jvm.signature
import java.lang.reflect.Method
import java.lang.reflect.Modifier

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

internal class ValueClassBoxDeserializer<T : Any>(
    private val creator: Method,
    clazz: Class<T>
) : StdDeserializer<T>(clazz) {
    private val inputType: Class<*> = creator.parameterTypes[0]
    private val boxMethod: Method = clazz
        .getDeclaredMethod("box-impl", clazz.getDeclaredMethod("unbox-impl").returnType)
        .apply { if (!this.isAccessible) this.isAccessible = true }

    init {
        creator.apply { if (!this.isAccessible) this.isAccessible = true }
    }

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): T {
        val input = p.readValueAs(inputType)

        // To instantiate the value class in the same way as other classes,
        // it is necessary to call creator(e.g. constructor-impl) -> box-impl in that order.
        @Suppress("UNCHECKED_CAST")
        return boxMethod.invoke(null, creator.invoke(null, input)) as T
    }
}

private fun findValueCreator(clazz: Class<*>, kmClass: KmClass): Method? {
    val primaryKmConstructorSignature =
        kmClass.constructors.first { !Flag.Constructor.IS_SECONDARY(it.flags) }.signature
    var primaryConstructor: Method? = null

    clazz.declaredMethods.forEach { method ->
        if (Modifier.isStatic(method.modifiers)) {
            if (method.hasCreatorAnnotation()) {
                // Do nothing if a correctly functioning Creator is defined
                return method.takeIf { clazz != method.returnType }
            } else if (method.toSignature() == primaryKmConstructorSignature) {
                // Here the PRIMARY constructor is invoked, ignoring visibility.
                // This behavior is the same as the normal class deserialization by kotlin-module.
                primaryConstructor = method
            }
        }
    }

    return primaryConstructor
}

internal class KotlinDeserializers(private val cache: ReflectionCache) : Deserializers.Base() {
    override fun findBeanDeserializer(
        type: JavaType,
        config: DeserializationConfig?,
        beanDesc: BeanDescription
    ): JsonDeserializer<*>? {
        val rawClass = type.rawClass

        return when {
            rawClass == Sequence::class.java -> SequenceDeserializer
            rawClass == Regex::class.java -> RegexDeserializer
            rawClass == UByte::class.java -> UByteDeserializer
            rawClass == UShort::class.java -> UShortDeserializer
            rawClass == UInt::class.java -> UIntDeserializer
            rawClass == ULong::class.java -> ULongDeserializer
            rawClass.isUnboxableValueClass() -> findValueCreator(rawClass, cache.getKmClass(rawClass)!!)
                ?.let { ValueClassBoxDeserializer(it, rawClass) }
            else -> null
        }
    }
}
