package io.github.projectmapk.jackson.module.kogera

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.PropertyName
import com.fasterxml.jackson.databind.util.ClassUtil
import io.github.projectmapk.jackson.module.kogera.annotation.JsonKUnbox
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import kotlin.metadata.KmClass
import kotlin.metadata.KmClassifier
import kotlin.metadata.KmType
import kotlin.metadata.jvm.JvmMethodSignature
import kotlin.metadata.jvm.KotlinClassMetadata

internal typealias JavaDuration = java.time.Duration
internal typealias KotlinDuration = kotlin.time.Duration

internal fun Class<*>.toKmClass(): KmClass? = getAnnotation(METADATA_CLASS)?.let {
    (KotlinClassMetadata.readStrict(it) as KotlinClassMetadata.Class).kmClass
}

internal fun Class<*>.isUnboxableValueClass() = this.isAnnotationPresent(JVM_INLINE_CLASS)

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

internal fun KmType.reconstructClassOrNull(): Class<*>? = (classifier as? KmClassifier.Class)?.reconstructClassOrNull()
internal fun KmClassifier.Class.reconstructClassOrNull(): Class<*>? = runCatching { name.reconstructClass() }.getOrNull()

internal fun AnnotatedElement.hasCreatorAnnotation(): Boolean = getAnnotation(JSON_CREATOR_CLASS)
    ?.let { it.mode != JsonCreator.Mode.DISABLED }
    ?: false

// Instantiating Java Class as a static property is expected to improve first-time execution performance.
// However, maybe this improvement is limited to Java Classes that are not used to initialize static content.
// Also, for classes that are read at the time of initialization of static content or module initialization,
// optimization seems unnecessary because caching is effective.
internal val METADATA_CLASS = Metadata::class.java
internal val JVM_INLINE_CLASS = JvmInline::class.java
internal val JSON_CREATOR_CLASS = JsonCreator::class.java
internal val JSON_PROPERTY_CLASS = JsonProperty::class.java
internal val JSON_K_UNBOX_CLASS = JsonKUnbox::class.java
internal val KOTLIN_DURATION_CLASS = KotlinDuration::class.java
internal val CLOSED_FLOATING_POINT_RANGE_CLASS = ClosedFloatingPointRange::class.java
internal val INT_CLASS = Int::class.java
internal val LONG_CLASS = Long::class.java
internal val STRING_CLASS = String::class.java
internal val JAVA_UUID_CLASS = java.util.UUID::class.java
internal val ANY_CLASS = Any::class.java

internal val ANY_TO_ANY_METHOD_TYPE by lazy { MethodType.methodType(ANY_CLASS, ANY_CLASS) }
internal val ANY_TO_INT_METHOD_TYPE by lazy { MethodType.methodType(INT_CLASS, ANY_CLASS) }
internal val ANY_TO_LONG_METHOD_TYPE by lazy { MethodType.methodType(LONG_CLASS, ANY_CLASS) }
internal val ANY_TO_STRING_METHOD_TYPE by lazy { MethodType.methodType(STRING_CLASS, ANY_CLASS) }
internal val ANY_TO_JAVA_UUID_METHOD_TYPE by lazy { MethodType.methodType(JAVA_UUID_CLASS, ANY_CLASS) }
internal val INT_TO_ANY_METHOD_TYPE by lazy { MethodType.methodType(ANY_CLASS, INT_CLASS) }
internal val LONG_TO_ANY_METHOD_TYPE by lazy { MethodType.methodType(ANY_CLASS, LONG_CLASS) }
internal val STRING_TO_ANY_METHOD_TYPE by lazy { MethodType.methodType(ANY_CLASS, STRING_CLASS) }
internal val JAVA_UUID_TO_ANY_METHOD_TYPE by lazy { MethodType.methodType(ANY_CLASS, JAVA_UUID_CLASS) }

internal fun unreflectWithAccessibilityModification(method: Method): MethodHandle = MethodHandles.lookup().unreflect(
    method.apply { ClassUtil.checkAndFixAccess(this, false) },
)
internal fun unreflectAsTypeWithAccessibilityModification(
    method: Method,
    type: MethodType,
): MethodHandle = unreflectWithAccessibilityModification(method).asType(type)

private val primitiveClassToDesc = mapOf(
    Byte::class.java to 'B',
    Char::class.java to 'C',
    Double::class.java to 'D',
    Float::class.java to 'F',
    INT_CLASS to 'I',
    LONG_CLASS to 'J',
    Short::class.java to 'S',
    Boolean::class.java to 'Z',
    Void.TYPE to 'V',
)

// -> this.name.replace(".", "/")
private fun Class<*>.descName(): String {
    val replaced = name.toCharArray().apply {
        for (i in indices) {
            if (this[i] == '.') this[i] = '/'
        }
    }
    return String(replaced)
}

private fun StringBuilder.appendDescriptor(clazz: Class<*>): StringBuilder = when {
    clazz.isPrimitive -> append(primitiveClassToDesc.getValue(clazz))
    clazz.isArray -> append('[').appendDescriptor(clazz.componentType)
    else -> append("L${clazz.descName()};")
}

// -> this.joinToString(separator = "", prefix = "(", postfix = ")") { it.descriptor }
internal fun Array<Class<*>>.toDescBuilder(): StringBuilder = this
    .fold(StringBuilder("(")) { acc, cur -> acc.appendDescriptor(cur) }
    .append(')')

internal fun Constructor<*>.toSignature(): JvmMethodSignature = JvmMethodSignature(
    "<init>",
    parameterTypes.toDescBuilder().append('V').toString(),
)

internal fun Method.toSignature(): JvmMethodSignature = JvmMethodSignature(
    this.name,
    parameterTypes.toDescBuilder().appendDescriptor(this.returnType).toString(),
)

// Delegate for calling package-private constructor
internal fun kotlinInvalidNullException(
    kotlinParameterName: String,
    valueClass: Class<*>,
    p: JsonParser,
    msg: String,
    pname: PropertyName,
) = KotlinInvalidNullException(kotlinParameterName, valueClass, p, msg, pname)
