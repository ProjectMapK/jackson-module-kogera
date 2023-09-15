package io.github.projectmapk.jackson.module.kogera

import kotlinx.metadata.ClassName
import kotlinx.metadata.Flags
import kotlinx.metadata.KmClass
import kotlinx.metadata.KmConstructor
import kotlinx.metadata.KmFunction
import kotlinx.metadata.KmProperty
import kotlinx.metadata.jvm.fieldSignature
import kotlinx.metadata.jvm.getterSignature
import kotlinx.metadata.jvm.signature
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

// Jackson Metadata Class
internal class JmClass(
    clazz: Class<*>,
    kmClass: KmClass,
    superJmClass: JmClass?,
    interfaceJmClasses: List<JmClass>
) {
    private val allPropsMap: Map<String, KmProperty> = mutableMapOf<String, KmProperty>().apply {
        kmClass.properties.forEach {
            this[it.name] = it
        }

        // Add properties of inherited classes and interfaces
        // If an `interface` is implicitly implemented by an abstract class,
        // it is necessary to obtain a more specific type, so always add it from the abstract class first.
        superJmClass?.allPropsMap?.forEach {
            this.putIfAbsent(it.key, it.value)
        }
        interfaceJmClasses.forEach { i ->
            i.allPropsMap.forEach {
                this.putIfAbsent(it.key, it.value)
            }
        }
    }

    val flags: Flags = kmClass.flags
    val constructors: List<KmConstructor> = kmClass.constructors
    val properties: List<KmProperty> = allPropsMap.values.toList()
    val sealedSubclasses: List<ClassName> = kmClass.sealedSubclasses
    private val companionPropName: String? = kmClass.companionObject
    val companion: CompanionObject? by lazy { companionPropName?.let { CompanionObject(clazz, it) } }

    fun findKmConstructor(constructor: Constructor<*>): KmConstructor? {
        val descHead = constructor.parameterTypes.toDescBuilder()
        val len = descHead.length
        val desc = CharArray(len + 1).apply {
            descHead.getChars(0, len, this, 0)
            this[len] = 'V'
        }.let { String(it) }

        // Only constructors that take a value class as an argument have a DefaultConstructorMarker on the Signature.
        val valueDesc = descHead
            .replace(len - 1, len, "Lkotlin/jvm/internal/DefaultConstructorMarker;)V")
            .toString()

        // Constructors always have the same name, so only desc is compared
        return constructors.find {
            val targetDesc = it.signature?.desc
            targetDesc == desc || targetDesc == valueDesc
        }
    }

    // Field name always matches property name
    fun findPropertyByField(field: Field): KmProperty? = allPropsMap[field.name]
        ?.takeIf { it.fieldSignature?.desc == field.desc() }

    fun findPropertyByGetter(getter: Method): KmProperty? {
        val getterName = getter.name
        return properties.find { it.getterSignature?.name == getterName }
    }

    internal class CompanionObject(
        declaringClass: Class<*>,
        companionObject: String
    ) {
        private val companionField: Field = declaringClass.getDeclaredField(companionObject)
        val type: Class<*> = companionField.type
        val isAccessible: Boolean = companionField.isAccessible
        private val functions by lazy { type.toKmClass()!!.functions }
        val instance: Any by lazy {
            // To prevent the call from failing, save the initial value and then rewrite the flag.
            if (!companionField.isAccessible) companionField.isAccessible = true
            companionField.get(null)
        }

        fun findFunctionByMethod(method: Method): KmFunction? {
            val signature = method.toSignature()
            return functions.find { it.signature == signature }
        }
    }
}
