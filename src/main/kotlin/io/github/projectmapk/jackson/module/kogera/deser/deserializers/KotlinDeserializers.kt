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
import io.github.projectmapk.jackson.module.kogera.ANY_CLASS
import io.github.projectmapk.jackson.module.kogera.ANY_TO_ANY_METHOD_TYPE
import io.github.projectmapk.jackson.module.kogera.GenericValueClassBoxConverter
import io.github.projectmapk.jackson.module.kogera.INT_CLASS
import io.github.projectmapk.jackson.module.kogera.IntValueClassBoxConverter
import io.github.projectmapk.jackson.module.kogera.JAVA_UUID_CLASS
import io.github.projectmapk.jackson.module.kogera.JavaUuidValueClassBoxConverter
import io.github.projectmapk.jackson.module.kogera.KotlinDuration
import io.github.projectmapk.jackson.module.kogera.LONG_CLASS
import io.github.projectmapk.jackson.module.kogera.LongValueClassBoxConverter
import io.github.projectmapk.jackson.module.kogera.ReflectionCache
import io.github.projectmapk.jackson.module.kogera.STRING_CLASS
import io.github.projectmapk.jackson.module.kogera.StringValueClassBoxConverter
import io.github.projectmapk.jackson.module.kogera.ValueClassBoxConverter
import io.github.projectmapk.jackson.module.kogera.deser.JavaToKotlinDurationConverter
import io.github.projectmapk.jackson.module.kogera.deser.WrapsNullableValueClassDeserializer
import io.github.projectmapk.jackson.module.kogera.hasCreatorAnnotation
import io.github.projectmapk.jackson.module.kogera.isUnboxableValueClass
import io.github.projectmapk.jackson.module.kogera.jmClass.JmClass
import io.github.projectmapk.jackson.module.kogera.toSignature
import io.github.projectmapk.jackson.module.kogera.unreflectAsTypeWithAccessibilityModification
import io.github.projectmapk.jackson.module.kogera.unreflectWithAccessibilityModification
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.UUID

internal object SequenceDeserializer : StdDeserializer<Sequence<*>>(Sequence::class.java) {
    private fun readResolve(): Any = SequenceDeserializer

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Sequence<*> = ctxt
        .readValue(p, List::class.java)
        .asSequence()
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
                        "Expected an array of strings for RegexOptions, but type was ${node.nodeType}",
                    )
                }
                optionsNode.elements().asSequence().map { RegexOption.valueOf(it.asText()) }.toSet()
            } else {
                emptySet()
            }
            return Regex(pattern, options)
        } else {
            throw IllegalStateException(
                "Expected a string or an object to deserialize a Regex, but type was ${node.nodeType}",
            )
        }
    }
}

internal object UByteDeserializer : StdDeserializer<UByte>(UByte::class.java) {
    private fun readResolve(): Any = UByteDeserializer

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext) = UByteChecker
        .readWithRangeCheck(p, p.intValue)
}

internal object UShortDeserializer : StdDeserializer<UShort>(UShort::class.java) {
    private fun readResolve(): Any = UShortDeserializer

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext) = UShortChecker
        .readWithRangeCheck(p, p.intValue)
}

internal object UIntDeserializer : StdDeserializer<UInt>(UInt::class.java) {
    private fun readResolve(): Any = UIntDeserializer

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext) = UIntChecker
        .readWithRangeCheck(p, p.longValue)
}

internal object ULongDeserializer : StdDeserializer<ULong>(ULong::class.java) {
    private fun readResolve(): Any = ULongDeserializer

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext) = ULongChecker
        .readWithRangeCheck(p, p.bigIntegerValue)
}

// If the creator does not perform type conversion, implement a unique deserializer for each for fast invocation.
internal sealed class NoConversionCreatorBoxDeserializer<S, D : Any>(
    creator: Method,
    converter: ValueClassBoxConverter<S, D>,
) : WrapsNullableValueClassDeserializer<D>(converter.boxedClass) {
    protected abstract val inputType: Class<*>
    protected val handle: MethodHandle = MethodHandles
        .filterReturnValue(unreflectWithAccessibilityModification(creator), converter.boxHandle)

    // Since the input to handle must be strict, invoke should be implemented in each class
    protected abstract fun invokeExact(value: S): D

    // Cache the result of wrapping null, since the result is always expected to be the same.
    @get:JvmName("boxedNullValue")
    private val boxedNullValue: D by lazy {
        // For the sake of commonality, it is unavoidably called without checking.
        // It is controlled by KotlinValueInstantiator, so it is not expected to reach this branch.
        @Suppress("UNCHECKED_CAST")
        invokeExact(null as S)
    }

    final override fun getBoxedNullValue(): D = boxedNullValue

    final override fun deserialize(p: JsonParser, ctxt: DeserializationContext): D {
        @Suppress("UNCHECKED_CAST")
        return invokeExact(p.readValueAs(inputType) as S)
    }

    internal class WrapsInt<D : Any>(
        creator: Method,
        converter: IntValueClassBoxConverter<D>,
    ) : NoConversionCreatorBoxDeserializer<Int, D>(creator, converter) {
        override val inputType get() = INT_CLASS

        @Suppress("UNCHECKED_CAST")
        override fun invokeExact(value: Int): D = handle.invokeExact(value) as D
    }

    internal class WrapsLong<D : Any>(
        creator: Method,
        converter: LongValueClassBoxConverter<D>,
    ) : NoConversionCreatorBoxDeserializer<Long, D>(creator, converter) {
        override val inputType get() = LONG_CLASS

        @Suppress("UNCHECKED_CAST")
        override fun invokeExact(value: Long): D = handle.invokeExact(value) as D
    }

    internal class WrapsString<D : Any>(
        creator: Method,
        converter: StringValueClassBoxConverter<D>,
    ) : NoConversionCreatorBoxDeserializer<String?, D>(creator, converter) {
        override val inputType get() = STRING_CLASS

        @Suppress("UNCHECKED_CAST")
        override fun invokeExact(value: String?): D = handle.invokeExact(value) as D
    }

    internal class WrapsJavaUuid<D : Any>(
        creator: Method,
        converter: JavaUuidValueClassBoxConverter<D>,
    ) : NoConversionCreatorBoxDeserializer<UUID?, D>(creator, converter) {
        override val inputType get() = JAVA_UUID_CLASS

        @Suppress("UNCHECKED_CAST")
        override fun invokeExact(value: UUID?): D = handle.invokeExact(value) as D
    }

    companion object {
        fun <S, D : Any> create(creator: Method, converter: ValueClassBoxConverter.Specified<S, D>) = when (converter) {
            is IntValueClassBoxConverter -> WrapsInt(creator, converter)
            is LongValueClassBoxConverter -> WrapsLong(creator, converter)
            is StringValueClassBoxConverter -> WrapsString(creator, converter)
            is JavaUuidValueClassBoxConverter -> WrapsJavaUuid(creator, converter)
        }
    }
}

// Even if the creator performs type conversion, it is distinguished
// because a speedup due to rtype matching of filterReturnValue can be expected for the specified type.
internal class HasConversionCreatorWrapsSpecifiedBoxDeserializer<S, D : Any>(
    creator: Method,
    private val inputType: Class<*>,
    converter: ValueClassBoxConverter<S, D>,
) : WrapsNullableValueClassDeserializer<D>(converter.boxedClass) {
    private val handle: MethodHandle

    init {
        val unreflect = unreflectWithAccessibilityModification(creator).run {
            asType(type().changeParameterType(0, ANY_CLASS))
        }
        handle = MethodHandles.filterReturnValue(unreflect, converter.boxHandle)
    }

    // Cache the result of wrapping null, since the result is always expected to be the same.
    @get:JvmName("boxedNullValue")
    private val boxedNullValue: D by lazy { instantiate(null) }

    override fun getBoxedNullValue(): D = boxedNullValue

    // To instantiate the value class in the same way as other classes,
    // it is necessary to call creator(e.g. constructor-impl) -> box-impl in that order.
    // Input is null only when called from KotlinValueInstantiator.
    @Suppress("UNCHECKED_CAST")
    private fun instantiate(input: Any?): D = handle.invokeExact(input) as D

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): D {
        val input = p.readValueAs(inputType)
        return instantiate(input)
    }
}

internal class WrapsAnyValueClassBoxDeserializer<S, D : Any>(
    creator: Method,
    private val inputType: Class<*>,
    converter: GenericValueClassBoxConverter<S, D>,
) : WrapsNullableValueClassDeserializer<D>(converter.boxedClass) {
    private val handle: MethodHandle

    init {
        val unreflect = unreflectAsTypeWithAccessibilityModification(creator, ANY_TO_ANY_METHOD_TYPE)
        handle = MethodHandles.filterReturnValue(unreflect, converter.boxHandle)
    }

    // Cache the result of wrapping null, since the result is always expected to be the same.
    @get:JvmName("boxedNullValue")
    private val boxedNullValue: D by lazy { instantiate(null) }

    override fun getBoxedNullValue(): D = boxedNullValue

    // To instantiate the value class in the same way as other classes,
    // it is necessary to call creator(e.g. constructor-impl) -> box-impl in that order.
    // Input is null only when called from KotlinValueInstantiator.
    @Suppress("UNCHECKED_CAST")
    private fun instantiate(input: Any?): D = handle.invokeExact(input) as D

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): D {
        val input = p.readValueAs(inputType)
        return instantiate(input)
    }
}

private fun invalidCreatorMessage(m: Method): String = "The argument size of a Creator that does not return a " +
    "value class on the JVM must be 1, please fix it or use JsonDeserializer.\n" +
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
    private val useJavaDurationConversion: Boolean,
) : SimpleDeserializers() {
    override fun findBeanDeserializer(
        type: JavaType,
        config: DeserializationConfig?,
        beanDesc: BeanDescription,
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
                val unboxedClass = it.returnType
                val converter = cache.getValueClassBoxConverter(unboxedClass, rawClass)

                when (converter) {
                    is ValueClassBoxConverter.Specified -> {
                        val inputType = it.parameterTypes[0]

                        if (inputType == unboxedClass) {
                            NoConversionCreatorBoxDeserializer.create(it, converter)
                        } else {
                            HasConversionCreatorWrapsSpecifiedBoxDeserializer(it, inputType, converter)
                        }
                    }
                    is GenericValueClassBoxConverter ->
                        WrapsAnyValueClassBoxDeserializer(it, it.parameterTypes[0], converter)
                }
            }
            else -> null
        }
    }
}
