package io.github.projectmapk.jackson.module.kogera

import kotlinx.metadata.KmClass
import kotlinx.metadata.KmConstructor
import kotlinx.metadata.KmProperty
import kotlinx.metadata.jvm.getterSignature
import kotlinx.metadata.jvm.signature
import java.lang.reflect.Constructor
import java.lang.reflect.Method

// Jackson Metadata Class
internal class JmClass(val kmClass: KmClass) {
    val constructors: List<KmConstructor> = kmClass.constructors
    val properties: List<KmProperty> = kmClass.properties

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

    companion object {
        fun createOrNull(clazz: Class<*>): JmClass? = clazz.toKmClass()?.let { JmClass(it) }
    }
}
