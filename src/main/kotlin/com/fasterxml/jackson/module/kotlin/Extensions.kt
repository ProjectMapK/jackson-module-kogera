package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.MappingIterator
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
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

fun kotlinModule(initializer: KotlinModule.Builder.() -> Unit = {}): KotlinModule {
    val builder = KotlinModule.Builder()
    builder.initializer()
    return builder.build()
}

fun jsonMapper(initializer: JsonMapper.Builder.() -> Unit = {}): JsonMapper {
    val builder = JsonMapper.builder()
    builder.initializer()
    return builder.build()
}

fun jacksonObjectMapper(): ObjectMapper = jsonMapper { addModule(kotlinModule()) }
fun jacksonMapperBuilder(): JsonMapper.Builder = JsonMapper.builder().addModule(kotlinModule())

fun ObjectMapper.registerKotlinModule(): ObjectMapper = this.registerModule(kotlinModule())

inline fun <reified T> jacksonTypeRef(): TypeReference<T> = object: TypeReference<T>() {}

inline fun <reified T> ObjectMapper.readValue(jp: JsonParser): T = readValue(jp, jacksonTypeRef<T>())
inline fun <reified T> ObjectMapper.readValues(jp: JsonParser): MappingIterator<T> = readValues(jp, jacksonTypeRef<T>())

inline fun <reified T> ObjectMapper.readValue(src: File): T = readValue(src, jacksonTypeRef<T>())
inline fun <reified T> ObjectMapper.readValue(src: URL): T = readValue(src, jacksonTypeRef<T>())
inline fun <reified T> ObjectMapper.readValue(content: String): T = readValue(content, jacksonTypeRef<T>())
inline fun <reified T> ObjectMapper.readValue(src: Reader): T = readValue(src, jacksonTypeRef<T>())
inline fun <reified T> ObjectMapper.readValue(src: InputStream): T = readValue(src, jacksonTypeRef<T>())
inline fun <reified T> ObjectMapper.readValue(src: ByteArray): T = readValue(src, jacksonTypeRef<T>())

inline fun <reified T> ObjectMapper.treeToValue(n: TreeNode): T = readValue(this.treeAsTokens(n), jacksonTypeRef<T>())
inline fun <reified T> ObjectMapper.convertValue(from: Any): T = convertValue(from, jacksonTypeRef<T>())

inline fun <reified T> ObjectReader.readValueTyped(jp: JsonParser): T = readValue(jp, jacksonTypeRef<T>())
inline fun <reified T> ObjectReader.readValuesTyped(jp: JsonParser): Iterator<T> = readValues(jp, jacksonTypeRef<T>())
inline fun <reified T> ObjectReader.treeToValue(n: TreeNode): T? = readValue(this.treeAsTokens(n), jacksonTypeRef<T>())

operator fun ArrayNode.plus(element: Boolean) { add(element) }
operator fun ArrayNode.plus(element: Short) { add(element) }
operator fun ArrayNode.plus(element: Int) { add(element) }
operator fun ArrayNode.plus(element: Long) { add(element) }
operator fun ArrayNode.plus(element: Float) { add(element) }
operator fun ArrayNode.plus(element: Double) { add(element) }
operator fun ArrayNode.plus(element: BigDecimal) { add(element) }
operator fun ArrayNode.plus(element: BigInteger) { add(element) }
operator fun ArrayNode.plus(element: String) { add(element) }
operator fun ArrayNode.plus(element: ByteArray) { add(element) }
operator fun ArrayNode.plus(element: JsonNode) { add(element) }
operator fun ArrayNode.plus(elements: ArrayNode) { addAll(elements) }
operator fun ArrayNode.plusAssign(element: Boolean) { add(element) }
operator fun ArrayNode.plusAssign(element: Short) { add(element) }
operator fun ArrayNode.plusAssign(element: Int) { add(element) }
operator fun ArrayNode.plusAssign(element: Long) { add(element) }
operator fun ArrayNode.plusAssign(element: Float) { add(element) }
operator fun ArrayNode.plusAssign(element: Double) { add(element) }
operator fun ArrayNode.plusAssign(element: BigDecimal) { add(element) }
operator fun ArrayNode.plusAssign(element: BigInteger) { add(element) }
operator fun ArrayNode.plusAssign(element: String) { add(element) }
operator fun ArrayNode.plusAssign(element: ByteArray) { add(element) }
operator fun ArrayNode.plusAssign(element: JsonNode) { add(element) }
operator fun ArrayNode.plusAssign(elements: ArrayNode) { addAll(elements) }
operator fun ArrayNode.minus(index: Int) { remove(index) }
operator fun ArrayNode.minusAssign(index: Int) { remove(index) }

operator fun ObjectNode.minus(field: String) { remove(field) }
operator fun ObjectNode.minus(fields: Collection<String>) { remove(fields) }
operator fun ObjectNode.minusAssign(field: String) { remove(field) }
operator fun ObjectNode.minusAssign(fields: Collection<String>) { remove(fields) }

operator fun JsonNode.contains(field: String) = has(field)
operator fun JsonNode.contains(index: Int) = has(index)

public fun <T : Any> SimpleModule.addSerializer(
    kClass: KClass<T>, serializer: JsonSerializer<T>
): SimpleModule = this.apply {
    kClass.javaPrimitiveType?.let { addSerializer(it, serializer) }
    addSerializer(kClass.javaObjectType, serializer)
}

public fun <T : Any> SimpleModule.addDeserializer(
    kClass: KClass<T>, deserializer: JsonDeserializer<T>
): SimpleModule = this.apply {
    kClass.javaPrimitiveType?.let { addDeserializer(it, deserializer) }
    addDeserializer(kClass.javaObjectType, deserializer)
}
