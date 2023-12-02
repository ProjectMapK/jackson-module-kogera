package io.github.projectmapk.jackson.module.kogera.deser.deserializers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException
import com.fasterxml.jackson.databind.module.SimpleDeserializers
import io.github.projectmapk.jackson.module.kogera.JmClass
import io.github.projectmapk.jackson.module.kogera.KotlinDuration
import io.github.projectmapk.jackson.module.kogera.ReflectionCache
import io.github.projectmapk.jackson.module.kogera.ValueClassBoxConverter
import io.github.projectmapk.jackson.module.kogera.deser.JavaToKotlinDurationConverter
import io.github.projectmapk.jackson.module.kogera.hasCreatorAnnotation
import io.github.projectmapk.jackson.module.kogera.isUnboxableValueClass
import io.github.projectmapk.jackson.module.kogera.toSignature
import kotlinx.metadata.isSecondary
import kotlinx.metadata.jvm.signature
import java.lang.reflect.Method
import java.lang.reflect.Modifier

internal object SequenceDeserializer : StdDeserializer<Sequence<*>>(Sequence::class.java) {
    private fun readResolve(): Any = SequenceDeserializer

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Sequence<*> {
        return ctxt.readValue(p, List::class.java).asSequence()
    }
}

internal object RegexDeserializer : StdDeserializer<Regex>(Regex::class.java) {
    private fun readResolve(): Any = RegexDeserializer

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
    private fun readResolve(): Any = UByteDeserializer

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext) =
        UByteChecker.readWithRangeCheck(p, p.intValue)
}

internal object UShortDeserializer : StdDeserializer<UShort>(UShort::class.java) {
    private fun readResolve(): Any = UShortDeserializer

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext) =
        UShortChecker.readWithRangeCheck(p, p.intValue)
}

internal object UIntDeserializer : StdDeserializer<UInt>(UInt::class.java) {
    private fun readResolve(): Any = UIntDeserializer

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext) =
        UIntChecker.readWithRangeCheck(p, p.longValue)
}

internal object ULongDeserializer : StdDeserializer<ULong>(ULong::class.java) {
    private fun readResolve(): Any = ULongDeserializer

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext) =
        ULongChecker.readWithRangeCheck(p, p.bigIntegerValue)
}

internal class ValueClassBoxDeserializer<S, D : Any>(
    private val creator: Method,
    private val converter: ValueClassBoxConverter<S, D>
) : StdDeserializer<D>(converter.valueClass) {
    private val inputType: Class<*> = creator.parameterTypes[0]

    init {
        creator.apply { if (!this.isAccessible) this.isAccessible = true }
    }

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): D {
        val input = p.readValueAs(inputType)

        // To instantiate the value class in the same way as other classes,
        // it is necessary to call creator(e.g. constructor-impl) -> box-impl in that order.
        @Suppress("UNCHECKED_CAST")
        return converter.convert(creator.invoke(null, input) as S)
    }
}

private fun invalidCreatorMessage(m: Method): String =
    "The argument size of a Creator that does not return a value class on the JVM must be 1, " +
        "please fix it or use JsonDeserializer.\n" +
        "Detected: ${m.parameters.joinToString(prefix = "${m.name}(", separator = ", ", postfix = ")") { it.name }}"

private fun findValueCreator(type: JavaType, clazz: Class<*>, jmClass: JmClass): Method? {
    val primaryKmConstructorSignature =
        jmClass.constructors.first { !it.isSecondary }.signature
    var primaryConstructor: Method? = null

    clazz.declaredMethods.forEach { method ->
        if (Modifier.isStatic(method.modifiers)) {
            if (method.hasCreatorAnnotation()) {
                // Do nothing if a correctly functioning Creator is defined
                return method.takeIf { clazz != method.returnType }?.apply {
                    // Creator with an argument size not equal to 1 is currently not supported.
                    if (parameterCount != 1) {
                        throw InvalidDefinitionException.from(null as JsonParser?, invalidCreatorMessage(method), type)
                    }
                }
            } else if (method.toSignature() == primaryKmConstructorSignature) {
                // Here the PRIMARY constructor is invoked, ignoring visibility.
                // This behavior is the same as the normal class deserialization by kotlin-module.
                primaryConstructor = method
            }
        }
    }

    return primaryConstructor
}

internal class KotlinDeserializers(
    private val cache: ReflectionCache,
    private val useJavaDurationConversion: Boolean
) : SimpleDeserializers() {
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
            rawClass == KotlinDuration::class.java ->
                JavaToKotlinDurationConverter.takeIf { useJavaDurationConversion }?.delegatingDeserializer
            rawClass.isUnboxableValueClass() -> findValueCreator(type, rawClass, cache.getJmClass(rawClass)!!)?.let {
                val unboxedClass = cache.getValueClassUnboxConverter(rawClass).unboxedClass
                val converter = cache.getValueClassBoxConverter(unboxedClass, rawClass)
                ValueClassBoxDeserializer(it, converter)
            }
            else -> null
        }
    }
}
