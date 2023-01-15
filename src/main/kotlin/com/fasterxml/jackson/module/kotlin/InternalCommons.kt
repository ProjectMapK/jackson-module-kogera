package com.fasterxml.jackson.module.kotlin

import kotlinx.metadata.KmClass
import kotlinx.metadata.KmConstructor
import kotlinx.metadata.KmValueParameter
import kotlinx.metadata.jvm.JvmFieldSignature
import kotlinx.metadata.jvm.JvmMethodSignature
import kotlinx.metadata.jvm.KotlinClassMetadata
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

internal fun Field.toSignature(): JvmFieldSignature =
    JvmFieldSignature(this.name, this.type.descriptor)

internal fun List<KmValueParameter>.hasVarargParam(): Boolean =
    lastOrNull()?.let { it.varargElementType != null } ?: false

internal val defaultConstructorMarker: Class<*> by lazy {
    Class.forName("kotlin.jvm.internal.DefaultConstructorMarker")
}

// Kotlin-specific types such as kotlin.String will result in an error,
// but are ignored because they do not result in errors in internal use cases.
internal fun String.reconstructClass(): Class<*> = Class.forName(this.replace(".", "$").replace("/", "."))

internal fun KmClass.findKmConstructor(constructor: Constructor<*>): KmConstructor? {
    // Constructors always have the same name, so only desc is compared
    val desc = constructor.parameterTypes.toDescString() + "V"
    return constructors.find { it.signature?.desc == desc }
}
