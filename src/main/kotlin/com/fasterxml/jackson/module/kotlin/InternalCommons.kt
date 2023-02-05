package com.fasterxml.jackson.module.kotlin

import kotlinx.metadata.Flag
import kotlinx.metadata.KmClass
import kotlinx.metadata.KmClassifier
import kotlinx.metadata.KmConstructor
import kotlinx.metadata.KmProperty
import kotlinx.metadata.KmType
import kotlinx.metadata.KmValueParameter
import kotlinx.metadata.jvm.JvmFieldSignature
import kotlinx.metadata.jvm.JvmMethodSignature
import kotlinx.metadata.jvm.KotlinClassMetadata
import kotlinx.metadata.jvm.getterSignature
import kotlinx.metadata.jvm.signature
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

internal fun Class<*>.isUnboxableValueClass() = annotations.any { it is JvmInline }

internal fun Class<*>.toKmClass(): KmClass? = annotations
    .filterIsInstance<Metadata>()
    .firstOrNull()
    ?.let { KotlinClassMetadata.read(it) as KotlinClassMetadata.Class }
    ?.toKmClass()

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

private fun StringBuilder.appendDescriptor(clazz: Class<*>): StringBuilder = when {
    clazz.isPrimitive -> append(primitiveClassToDesc.getValue(clazz))
    clazz.isArray -> append('[').appendDescriptor(clazz.componentType)
    else -> append("L${clazz.name.replace('.', '/')};")
}

internal fun Array<Class<*>>.toDescBuilder(): StringBuilder = this
    .fold(StringBuilder("(")) { acc, cur -> acc.appendDescriptor(cur) }
    .append(')')

internal fun Constructor<*>.toSignature(): JvmMethodSignature =
    JvmMethodSignature("<init>", parameterTypes.toDescBuilder().append('V').toString())

internal fun Method.toSignature(): JvmMethodSignature =
    JvmMethodSignature(this.name, parameterTypes.toDescBuilder().appendDescriptor(this.returnType).toString())

private val Class<*>.descriptor: String
    get() = when {
        isPrimitive -> primitiveClassToDesc.getValue(this).toString()
        isArray -> "[${componentType.descriptor}"
        else -> "L${name.replace('.', '/')};"
    }

internal fun Field.toSignature(): JvmFieldSignature =
    JvmFieldSignature(this.name, this.type.descriptor)

internal fun List<KmValueParameter>.hasVarargParam(): Boolean =
    lastOrNull()?.let { it.varargElementType != null } ?: false

internal val defaultConstructorMarker: Class<*> by lazy {
    Class.forName("kotlin.jvm.internal.DefaultConstructorMarker")
}

// Kotlin-specific types such as kotlin.String will result in an error,
// but are ignored because they do not result in errors in internal use cases.
internal fun String.reconstructClass(): Class<*> {
    // -> this.replace(".", "$").replace("/", ".")
    val replaced = this.toCharArray().apply {
        for (i in indices) {
            val c = this[i]

            if (c == '.') {
                this[i] = '$'
            } else if (c == '/') {
                this[i] = '.'
            }
        }
    }

    return Class.forName(String(replaced))
}

internal fun KmType.reconstructClassOrNull(): Class<*>? = (classifier as? KmClassifier.Class)
    ?.let { kotlin.runCatching { it.name.reconstructClass() }.getOrNull() }

internal fun KmClass.findKmConstructor(constructor: Constructor<*>): KmConstructor? {
    val descHead = constructor.parameterTypes.toDescBuilder()
    val desc = CharArray(descHead.length + 1).apply {
        descHead.getChars(0, descHead.length, this, 0)
        this[this.lastIndex] = 'V'
    }.let { String(it) }

    // Only constructors that take a value class as an argument have a DefaultConstructorMarker on the Signature.
    val valueDesc = descHead
        .deleteCharAt(descHead.length - 1)
        .append("Lkotlin/jvm/internal/DefaultConstructorMarker;)V")
        .toString()

    // Constructors always have the same name, so only desc is compared
    return constructors.find {
        val targetDesc = it.signature?.desc
        targetDesc == desc || targetDesc == valueDesc
    }
}

internal fun KmClass.findPropertyByGetter(getter: Method): KmProperty? {
    val signature = getter.toSignature()
    return properties.find { it.getterSignature == signature }
}

internal fun KmType.isNullable(): Boolean = Flag.Type.IS_NULLABLE(this.flags)
