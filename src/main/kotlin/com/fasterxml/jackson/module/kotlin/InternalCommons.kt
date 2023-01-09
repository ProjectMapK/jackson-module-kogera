package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.databind.JsonMappingException
import kotlinx.metadata.KmClass
import kotlinx.metadata.KmValueParameter
import kotlinx.metadata.jvm.JvmMethodSignature
import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.util.*

internal fun JsonMappingException.wrapWithPath(refFrom: Any?, refFieldName: String) = JsonMappingException.wrapWithPath(this, refFrom, refFieldName)
internal fun JsonMappingException.wrapWithPath(refFrom: Any?, index: Int) = JsonMappingException.wrapWithPath(this, refFrom, index)

internal fun Int.toBitSet(): BitSet {
    var i = this
    var index = 0
    val bits = BitSet(Int.SIZE_BITS)
    while (i != 0) {
        if (i % 2 != 0) {
            bits.set(index)
        }
        ++index
        i = i shr 1
    }
    return bits
}

internal fun Class<*>.isKotlinClass(): Boolean = declaredAnnotations.any { it is Metadata }
internal fun Class<*>.isUnboxableValueClass() = annotations.any { it is JvmInline }

internal fun Class<*>.toKmClass(): KmClass = annotations
    .filterIsInstance<Metadata>()
    .first()
    .let {
        KotlinClassMetadata.read(
            KotlinClassHeader(
                it.kind,
                it.metadataVersion,
                it.data1,
                it.data2,
                it.extraString,
                it.packageName,
                it.extraInt
            )
        ) as KotlinClassMetadata.Class
    }.toKmClass()

private val primitiveClassToDesc by lazy {
    mapOf(
        Byte::class.javaPrimitiveType to 'B',
        Char::class.javaPrimitiveType to 'C',
        Double::class.javaPrimitiveType to 'D',
        Float::class.javaPrimitiveType to 'F',
        Int::class.javaPrimitiveType to 'I',
        Long::class.javaPrimitiveType to 'J',
        Short::class.javaPrimitiveType to 'S',
        Boolean::class.javaPrimitiveType to 'Z',
        Void::class.javaPrimitiveType to 'V'
    )
}

private val Class<*>.descriptor: String
    get() = when {
        isPrimitive -> primitiveClassToDesc.getValue(this).toString()
        isArray -> "[${componentType.descriptor}"
        else -> "L${name.replace('.', '/')};"
    }

internal fun Array<Class<*>>.toDescString(): String =
    joinToString(separator = "", prefix = "(", postfix = ")") { it.descriptor }

internal fun Constructor<*>.toSignature(): JvmMethodSignature =
    JvmMethodSignature("<init>", parameterTypes.toDescString() + "V")

internal fun Method.toSignature(): JvmMethodSignature =
    JvmMethodSignature(this.name, parameterTypes.toDescString() + this.returnType.descriptor)

internal fun List<KmValueParameter>.hasVarargParam(): Boolean =
    lastOrNull()?.let { it.varargElementType != null } ?: false

internal val defaultConstructorMarker: Class<*> by lazy {
    Class.forName("kotlin.jvm.internal.DefaultConstructorMarker")
}
