package io.github.projectmapk.jackson.module.kogera

import kotlinx.metadata.ClassKind
import kotlinx.metadata.ClassName
import kotlinx.metadata.KmClass
import kotlinx.metadata.KmConstructor
import kotlinx.metadata.KmFunction
import kotlinx.metadata.KmProperty
import kotlinx.metadata.KmType
import kotlinx.metadata.jvm.getterSignature
import kotlinx.metadata.jvm.signature
import kotlinx.metadata.kind
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

// Jackson Metadata Class
internal sealed interface JmClass {
    class CompanionObject(declaringClass: Class<*>, companionObject: String) {
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

    val kind: ClassKind
    val constructors: List<KmConstructor>
    val sealedSubclasses: List<ClassName>
    val inlineClassUnderlyingType: KmType?
    val propertyNameSet: Set<String>
    val properties: List<KmProperty>
    val companion: CompanionObject?

    fun findKmConstructor(constructor: Constructor<*>): KmConstructor?
    fun findPropertyByField(field: Field): KmProperty?
    fun findPropertyByGetter(getter: Method): KmProperty?
}

private class JmClassImpl(
    clazz: Class<*>,
    kmClass: KmClass,
    superJmClass: JmClass?,
    interfaceJmClasses: List<JmClass>
) : JmClass {
    private val allPropsMap: Map<String, KmProperty>

    // Defined as non-lazy because it is always read in both serialization and deserialization
    override val properties: List<KmProperty>

    private val companionPropName: String? = kmClass.companionObject
    override val kind: ClassKind = kmClass.kind
    override val constructors: List<KmConstructor> = kmClass.constructors
    override val sealedSubclasses: List<ClassName> = kmClass.sealedSubclasses
    override val inlineClassUnderlyingType: KmType? = kmClass.inlineClassUnderlyingType

    init {
        // Add properties of inherited classes and interfaces
        // If an `interface` is implicitly implemented by an abstract class,
        // it is necessary to obtain a more specific type, so always add it from the abstract class first.
        val tempPropsMap = ((superJmClass as JmClassImpl?)?.allPropsMap?.toMutableMap() ?: mutableMapOf()).apply {
            kmClass.properties.forEach {
                this[it.name] = it
            }
        }

        allPropsMap = interfaceJmClasses.fold(tempPropsMap) { acc, cur ->
            val curProps = (cur as JmClassImpl).allPropsMap
            acc.apply {
                curProps.forEach { acc.putIfAbsent(it.key, it.value) }
            }
        }

        // Initialize after all properties have been read
        properties = allPropsMap.values.toList()
    }

    // computed props
    override val propertyNameSet: Set<String> get() = allPropsMap.keys
    override val companion: JmClass.CompanionObject? by lazy {
        companionPropName?.let { JmClass.CompanionObject(clazz, it) }
    }

    override fun findKmConstructor(constructor: Constructor<*>): KmConstructor? {
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
            val targetDesc = it.signature?.descriptor
            targetDesc == desc || targetDesc == valueDesc
        }
    }

    // Field name always matches property name
    override fun findPropertyByField(field: Field): KmProperty? = allPropsMap[field.name]

    override fun findPropertyByGetter(getter: Method): KmProperty? {
        val getterName = getter.name
        return properties.find { it.getterSignature?.name == getterName }
    }
}

internal fun JmClass(
    clazz: Class<*>,
    kmClass: KmClass,
    superJmClass: JmClass?,
    interfaceJmClasses: List<JmClass>
): JmClass = JmClassImpl(clazz, kmClass, superJmClass, interfaceJmClasses)
