package io.github.projectmapk.jackson.module.kogera.jmClass

import com.fasterxml.jackson.databind.util.ClassUtil
import io.github.projectmapk.jackson.module.kogera.reconstructClassOrNull
import io.github.projectmapk.jackson.module.kogera.toKmClass
import io.github.projectmapk.jackson.module.kogera.toSignature
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.metadata.ClassKind
import kotlin.metadata.ClassName
import kotlin.metadata.KmClass
import kotlin.metadata.isNullable
import kotlin.metadata.kind

// Jackson Metadata Class
internal sealed interface JmClass {
    class CompanionObject(declaringClass: Class<*>, companionObject: String) {
        private val companionField: Field = declaringClass.getDeclaredField(companionObject)
        val type: Class<*> = companionField.type
        val isAccessible: Boolean = companionField.isAccessible
        private val factoryFunctions by lazy {
            // Since it is a user-defined factory function that is processed,
            // it always has arguments and the return value is the same as declaringClass.
            type.toKmClass()!!.functions.mapNotNull { func ->
                func
                    .takeIf {
                        func.valueParameters.isNotEmpty() && func.returnType.reconstructClassOrNull() == declaringClass
                    }
                    ?.let { JmFunction(it) }
            }
        }
        val instance: Any by lazy {
            // To prevent the call from failing, save the initial value and then rewrite the flag.
            ClassUtil.checkAndFixAccess(companionField, false)
            companionField.get(null)
        }

        fun findFunctionByMethod(method: Method): JmFunction? {
            val signature = method.toSignature()
            return factoryFunctions.find { it.signature == signature }
        }
    }

    // region: from KmClass
    val kind: ClassKind
    val constructors: List<JmConstructor>
    val sealedSubclasses: List<ClassName>
    val propertyNameSet: Set<String>
    val properties: List<JmProperty>
    val companion: CompanionObject?
    // endregion

    // region: computed props
    val wrapsNullableIfValue: Boolean
    // endregion

    fun findJmConstructor(constructor: Constructor<*>): JmConstructor?
    fun findPropertyByField(field: Field): JmProperty?
    fun findPropertyByGetter(getter: Method): JmProperty?
}

private class JmClassImpl(
    clazz: Class<*>,
    kmClass: KmClass,
    superJmClass: JmClass?,
    interfaceJmClasses: List<JmClass>
) : JmClass {
    private val allPropsMap: Map<String, JmProperty>

    // Defined as non-lazy because it is always read in both serialization and deserialization
    override val properties: List<JmProperty>

    private val companionPropName: String? = kmClass.companionObject
    override val kind: ClassKind = kmClass.kind
    override val constructors: List<JmConstructor> = kmClass.constructors.map { JmConstructor(it) }
    override val sealedSubclasses: List<ClassName> = kmClass.sealedSubclasses

    override val wrapsNullableIfValue: Boolean = kmClass.inlineClassUnderlyingType?.isNullable ?: false

    init {
        // Add properties of inherited classes and interfaces
        // If an `interface` is implicitly implemented by an abstract class,
        // it is necessary to obtain a more specific type, so always add it from the abstract class first.
        val tempPropsMap = ((superJmClass as JmClassImpl?)?.allPropsMap?.toMutableMap() ?: mutableMapOf()).apply {
            kmClass.properties.forEach {
                this[it.name] = JmProperty(it)
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

    override fun findJmConstructor(constructor: Constructor<*>): JmConstructor? = constructors.find {
        it.isMetadataFor(constructor)
    }

    // Field name always matches property name
    override fun findPropertyByField(field: Field): JmProperty? = allPropsMap[field.name]

    override fun findPropertyByGetter(getter: Method): JmProperty? {
        val getterName = getter.name
        return properties.find { it.getterName == getterName }
    }
}

internal fun JmClass(
    clazz: Class<*>,
    kmClass: KmClass,
    superJmClass: JmClass?,
    interfaceJmClasses: List<JmClass>
): JmClass = JmClassImpl(clazz, kmClass, superJmClass, interfaceJmClasses)
