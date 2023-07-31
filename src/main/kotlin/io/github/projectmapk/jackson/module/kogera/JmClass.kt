package io.github.projectmapk.jackson.module.kogera

import kotlinx.metadata.ClassName
import kotlinx.metadata.Flags
import kotlinx.metadata.KmClass
import kotlinx.metadata.KmConstructor
import kotlinx.metadata.KmFunction
import kotlinx.metadata.KmProperty
import kotlinx.metadata.jvm.KotlinClassMetadata
import kotlinx.metadata.jvm.getterSignature
import kotlinx.metadata.jvm.signature
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

private fun Class<*>.toKmClass(): KmClass? = annotations
    .filterIsInstance<Metadata>()
    .firstOrNull()
    ?.let { KotlinClassMetadata.read(it) as KotlinClassMetadata.Class }
    ?.toKmClass()

// Jackson Metadata Class
internal class JmClass(
    private val clazz: Class<*>,
    kmClass: KmClass
) {
    val flags: Flags = kmClass.flags
    val constructors: List<KmConstructor> = kmClass.constructors
    val properties: List<KmProperty> = kmClass.properties
    private val functions: List<KmFunction> = kmClass.functions
    val sealedSubclasses: List<ClassName> = kmClass.sealedSubclasses
    private val companionPropName: String? = kmClass.companionObject
    val companion: CompanionObject? by lazy { companionPropName?.let { CompanionObject(clazz, it) } }

    fun findKmConstructor(constructor: Constructor<*>): KmConstructor? {
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

    fun findPropertyByGetter(getter: Method): KmProperty? {
        val signature = getter.toSignature()
        return properties.find { it.getterSignature == signature }
    }

    fun findFunctionByMethod(method: Method): KmFunction? {
        val signature = method.toSignature()
        return functions.find { it.signature == signature }
    }

    internal class CompanionObject(
        declaringClass: Class<*>,
        companionObject: String
    ) {
        private val companionField: Field = declaringClass.getDeclaredField(companionObject)
        val type: Class<*> = companionField.type
        val isAccessible: Boolean = companionField.isAccessible
        private val kmClass: KmClass by lazy { type.toKmClass()!! }
        val instance: Any by lazy {
            // To prevent the call from failing, save the initial value and then rewrite the flag.
            if (!companionField.isAccessible) companionField.isAccessible = true
            companionField.get(null)
        }

        fun findFunctionByMethod(method: Method): KmFunction? {
            val signature = method.toSignature()
            return kmClass.functions.find { it.signature == signature }
        }
    }

    companion object {
        fun createOrNull(clazz: Class<*>): JmClass? = clazz.toKmClass()?.let { JmClass(clazz, it) }
    }
}
