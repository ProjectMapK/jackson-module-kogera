package io.github.projectmapk.jackson.module.kogera

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.MappingIterator
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import com.fasterxml.jackson.databind.RuntimeJsonMappingException
import com.fasterxml.jackson.databind.cfg.MutableConfigOverride
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import java.io.File
import java.io.InputStream
import java.io.Reader
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URL
import kotlin.reflect.KClass

public fun kotlinModule(initializer: KotlinModule.Builder.() -> Unit = {}): KotlinModule {
    val builder = KotlinModule.Builder()
    builder.initializer()
    return builder.build()
}

public fun jsonMapper(initializer: JsonMapper.Builder.() -> Unit = {}): JsonMapper {
    val builder = JsonMapper.builder()
    builder.initializer()
    return builder.build()
}

// region: JvmOverloads is set for bytecode compatibility for versions below 2.17.
@JvmOverloads
public fun jacksonObjectMapper(initializer: KotlinModule.Builder.() -> Unit = {}): ObjectMapper = jsonMapper {
    addModule(kotlinModule(initializer))
}

@JvmOverloads
public fun jacksonMapperBuilder(initializer: KotlinModule.Builder.() -> Unit = {}): JsonMapper.Builder = JsonMapper
    .builder()
    .addModule(kotlinModule(initializer))

@JvmOverloads
public fun ObjectMapper.registerKotlinModule(
    initializer: KotlinModule.Builder.() -> Unit = {},
): ObjectMapper = this.registerModule(kotlinModule(initializer))
// endregion

public inline fun <reified T> jacksonTypeRef(): TypeReference<T> = object : TypeReference<T>() {}

@PublishedApi
internal inline fun <reified T> Any?.checkTypeMismatch(): T {
    // Basically, this check assumes that T is non-null and the value is null.
    // Since this can be caused by both input or ObjectMapper implementation errors,
    // a more abstract RuntimeJsonMappingException is thrown.
    if (this !is T) {
        val nullability = if (null is T) "?" else "(non-null)"

        // Since the databind implementation of MappingIterator throws RuntimeJsonMappingException,
        // JsonMappingException was not used to unify the behavior.
        throw RuntimeJsonMappingException(
            "Deserialized value did not match the specified type; " +
                "specified ${T::class.qualifiedName}$nullability but was ${this?.let { it::class.qualifiedName }}",
        )
    }
    return this
}

/**
 * Shorthand for [ObjectMapper.readValue].
 * @throws RuntimeJsonMappingException Especially if [T] is non-null and the value read is null.
 *   Other cases where the read value is of a different type than [T]
 *   due to an incorrect customization to [ObjectMapper].
 */
public inline fun <reified T> ObjectMapper.readValue(jp: JsonParser): T = readValue(jp, jacksonTypeRef<T>())
    .checkTypeMismatch()

/**
 * Shorthand for [ObjectMapper.readValues].
 * @throws RuntimeJsonMappingException Especially if [T] is non-null and the value read is null.
 *   Other cases where the read value is of a different type than [T]
 *   due to an incorrect customization to [ObjectMapper].
 */
public inline fun <reified T> ObjectMapper.readValues(jp: JsonParser): MappingIterator<T> {
    val values = readValues(jp, jacksonTypeRef<T>())

    return object : MappingIterator<T>(values) {
        override fun nextValue(): T = super.nextValue().checkTypeMismatch()
    }
}

/**
 * Shorthand for [ObjectMapper.readValue].
 * @throws RuntimeJsonMappingException Especially if [T] is non-null and the value read is null.
 *   Other cases where the read value is of a different type than [T]
 *   due to an incorrect customization to [ObjectMapper].
 */
public inline fun <reified T> ObjectMapper.readValue(src: File): T = readValue(src, jacksonTypeRef<T>())
    .checkTypeMismatch()

/**
 * Shorthand for [ObjectMapper.readValue].
 * @throws RuntimeJsonMappingException Especially if [T] is non-null and the value read is null.
 *   Other cases where the read value is of a different type than [T]
 *   due to an incorrect customization to [ObjectMapper].
 */
public inline fun <reified T> ObjectMapper.readValue(src: URL): T = readValue(src, jacksonTypeRef<T>())
    .checkTypeMismatch()

/**
 * Shorthand for [ObjectMapper.readValue].
 * @throws RuntimeJsonMappingException Especially if [T] is non-null and the value read is null.
 *   Other cases where the read value is of a different type than [T]
 *   due to an incorrect customization to [ObjectMapper].
 */
public inline fun <reified T> ObjectMapper.readValue(content: String): T = readValue(content, jacksonTypeRef<T>())
    .checkTypeMismatch()

/**
 * Shorthand for [ObjectMapper.readValue].
 * @throws RuntimeJsonMappingException Especially if [T] is non-null and the value read is null.
 *   Other cases where the read value is of a different type than [T]
 *   due to an incorrect customization to [ObjectMapper].
 */
public inline fun <reified T> ObjectMapper.readValue(src: Reader): T = readValue(src, jacksonTypeRef<T>())
    .checkTypeMismatch()

/**
 * Shorthand for [ObjectMapper.readValue].
 * @throws RuntimeJsonMappingException Especially if [T] is non-null and the value read is null.
 *   Other cases where the read value is of a different type than [T]
 *   due to an incorrect customization to [ObjectMapper].
 */
public inline fun <reified T> ObjectMapper.readValue(src: InputStream): T = readValue(src, jacksonTypeRef<T>())
    .checkTypeMismatch()

/**
 * Shorthand for [ObjectMapper.readValue].
 * @throws RuntimeJsonMappingException Especially if [T] is non-null and the value read is null.
 *   Other cases where the read value is of a different type than [T]
 *   due to an incorrect customization to [ObjectMapper].
 */
public inline fun <reified T> ObjectMapper.readValue(src: ByteArray): T = readValue(src, jacksonTypeRef<T>())
    .checkTypeMismatch()

/**
 * Shorthand for [ObjectMapper.readValue].
 * @throws RuntimeJsonMappingException Especially if [T] is non-null and the value read is null.
 *   Other cases where the read value is of a different type than [T]
 *   due to an incorrect customization to [ObjectMapper].
 */
public inline fun <reified T> ObjectMapper.treeToValue(
    n: TreeNode,
): T = readValue(this.treeAsTokens(n), jacksonTypeRef<T>()).checkTypeMismatch()

/**
 * Shorthand for [ObjectMapper.convertValue].
 * @throws RuntimeJsonMappingException Especially if [T] is non-null and the value read is null.
 *   Other cases where the read value is of a different type than [T]
 *   due to an incorrect customization to [ObjectMapper].
 */
public inline fun <reified T> ObjectMapper.convertValue(from: Any?): T = convertValue(from, jacksonTypeRef<T>())
    .checkTypeMismatch()

/**
 * Shorthand for [ObjectReader.readValue].
 * @throws RuntimeJsonMappingException Especially if [T] is non-null and the value read is null.
 *   Other cases where the read value is of a different type than [T]
 *   due to an incorrect customization to [ObjectReader].
 */
public inline fun <reified T> ObjectReader.readValueTyped(jp: JsonParser): T = readValue(jp, jacksonTypeRef<T>())
    .checkTypeMismatch()

/**
 * Shorthand for [ObjectReader.readValues].
 * @throws RuntimeJsonMappingException Especially if [T] is non-null and the value read is null.
 *   Other cases where the read value is of a different type than [T]
 *   due to an incorrect customization to [ObjectReader].
 */
public inline fun <reified T> ObjectReader.readValuesTyped(jp: JsonParser): Iterator<T> {
    val values = readValues(jp, jacksonTypeRef<T>())

    return object : Iterator<T> by values {
        override fun next(): T = values.next().checkTypeMismatch<T>()
    }
}
public inline fun <reified T> ObjectReader.treeToValue(
    n: TreeNode,
): T? = readValue(this.treeAsTokens(n), jacksonTypeRef<T>())

public inline fun <reified T, reified U> ObjectMapper.addMixIn(): ObjectMapper = addMixIn(T::class.java, U::class.java)
public inline fun <reified T, reified U> JsonMapper.Builder.addMixIn(): JsonMapper.Builder = addMixIn(
    T::class.java,
    U::class.java,
)

public operator fun ArrayNode.plus(element: Boolean) {
    add(element)
}
public operator fun ArrayNode.plus(element: Short) {
    add(element)
}
public operator fun ArrayNode.plus(element: Int) {
    add(element)
}
public operator fun ArrayNode.plus(element: Long) {
    add(element)
}
public operator fun ArrayNode.plus(element: Float) {
    add(element)
}
public operator fun ArrayNode.plus(element: Double) {
    add(element)
}
public operator fun ArrayNode.plus(element: BigDecimal) {
    add(element)
}
public operator fun ArrayNode.plus(element: BigInteger) {
    add(element)
}
public operator fun ArrayNode.plus(element: String) {
    add(element)
}
public operator fun ArrayNode.plus(element: ByteArray) {
    add(element)
}
public operator fun ArrayNode.plus(element: JsonNode) {
    add(element)
}
public operator fun ArrayNode.plus(elements: ArrayNode) {
    addAll(elements)
}
public operator fun ArrayNode.plusAssign(element: Boolean) {
    add(element)
}
public operator fun ArrayNode.plusAssign(element: Short) {
    add(element)
}
public operator fun ArrayNode.plusAssign(element: Int) {
    add(element)
}
public operator fun ArrayNode.plusAssign(element: Long) {
    add(element)
}
public operator fun ArrayNode.plusAssign(element: Float) {
    add(element)
}
public operator fun ArrayNode.plusAssign(element: Double) {
    add(element)
}
public operator fun ArrayNode.plusAssign(element: BigDecimal) {
    add(element)
}
public operator fun ArrayNode.plusAssign(element: BigInteger) {
    add(element)
}
public operator fun ArrayNode.plusAssign(element: String) {
    add(element)
}
public operator fun ArrayNode.plusAssign(element: ByteArray) {
    add(element)
}
public operator fun ArrayNode.plusAssign(element: JsonNode) {
    add(element)
}
public operator fun ArrayNode.plusAssign(elements: ArrayNode) {
    addAll(elements)
}
public operator fun ArrayNode.minus(index: Int) {
    remove(index)
}
public operator fun ArrayNode.minusAssign(index: Int) {
    remove(index)
}

public operator fun ObjectNode.minus(field: String) {
    remove(field)
}
public operator fun ObjectNode.minus(fields: Collection<String>) {
    remove(fields)
}
public operator fun ObjectNode.minusAssign(field: String) {
    remove(field)
}
public operator fun ObjectNode.minusAssign(fields: Collection<String>) {
    remove(fields)
}

public operator fun JsonNode.contains(field: String): Boolean = has(field)
public operator fun JsonNode.contains(index: Int): Boolean = has(index)

public fun <T : Any> SimpleModule.addSerializer(
    kClass: KClass<T>,
    serializer: JsonSerializer<T>,
): SimpleModule = this.apply {
    kClass.javaPrimitiveType?.let { addSerializer(it, serializer) }
    addSerializer(kClass.javaObjectType, serializer)
}

public fun <T : Any> SimpleModule.addDeserializer(
    kClass: KClass<T>,
    deserializer: JsonDeserializer<T>,
): SimpleModule = this.apply {
    kClass.javaPrimitiveType?.let { addDeserializer(it, deserializer) }
    addDeserializer(kClass.javaObjectType, deserializer)
}

public inline fun <reified T : Any> ObjectMapper.configOverride(): MutableConfigOverride = configOverride(T::class.java)
public inline fun <reified T : Any> Module.SetupContext.configOverride(): MutableConfigOverride = configOverride(T::class.java)
