package io.github.projectmapk.jackson.module.kogera

import kotlinx.metadata.ClassName
import kotlinx.metadata.ExperimentalContextReceivers
import kotlinx.metadata.Flags
import kotlinx.metadata.KmClassExtensionVisitor
import kotlinx.metadata.KmClassVisitor
import kotlinx.metadata.KmConstructor
import kotlinx.metadata.KmConstructorVisitor
import kotlinx.metadata.KmExtensionType
import kotlinx.metadata.KmFunction
import kotlinx.metadata.KmFunctionVisitor
import kotlinx.metadata.KmProperty
import kotlinx.metadata.KmPropertyVisitor
import kotlinx.metadata.KmType
import kotlinx.metadata.KmTypeAliasVisitor
import kotlinx.metadata.KmTypeParameterVisitor
import kotlinx.metadata.KmTypeVisitor
import kotlinx.metadata.KmVariance
import kotlinx.metadata.KmVersionRequirementVisitor
import kotlinx.metadata.flagsOf
import kotlinx.metadata.jvm.getterSignature
import kotlinx.metadata.jvm.signature
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

// KmClassVisitor with all processing disabled as much as possible to reduce load
internal sealed class ReducedKmClassVisitor : KmClassVisitor() {
    final override val delegate: KmClassVisitor? get() = null

    // from KmDeclarationContainerVisitor
    override fun visitFunction(flags: Flags, name: String): KmFunctionVisitor? = null
    override fun visitProperty(
        flags: Flags,
        name: String,
        getterFlags: Flags,
        setterFlags: Flags
    ): KmPropertyVisitor? = null
    override fun visitTypeAlias(flags: Flags, name: String): KmTypeAliasVisitor? = null
    override fun visitExtensions(type: KmExtensionType): KmClassExtensionVisitor? = null

    // from KmClassVisitor
    override fun visit(flags: Flags, name: ClassName) {}
    override fun visitTypeParameter(
        flags: Flags,
        name: String,
        id: Int,
        variance: KmVariance
    ): KmTypeParameterVisitor? = null
    override fun visitSupertype(flags: Flags): KmTypeVisitor? = null
    override fun visitConstructor(flags: Flags): KmConstructorVisitor? = null
    override fun visitCompanionObject(name: String) {}
    override fun visitNestedClass(name: String) {}
    override fun visitEnumEntry(name: String) {}
    override fun visitSealedSubclass(name: ClassName) {}
    override fun visitInlineClassUnderlyingPropertyName(name: String) {}
    override fun visitInlineClassUnderlyingType(flags: Flags): KmTypeVisitor? = null

    @OptIn(ExperimentalContextReceivers::class)
    override fun visitContextReceiverType(flags: Flags): KmTypeVisitor? = null
    override fun visitVersionRequirement(): KmVersionRequirementVisitor? = null
    override fun visitEnd() {}
}

internal class ReducedKmClass : ReducedKmClassVisitor() {
    var flags: Flags = flagsOf()
    val properties: MutableList<KmProperty> = ArrayList()
    val constructors: MutableList<KmConstructor> = ArrayList(1)
    var companionObject: String? = null
    val sealedSubclasses: MutableList<ClassName> = ArrayList(0)
    var inlineClassUnderlyingType: KmType? = null

    override fun visit(flags: Flags, name: ClassName) {
        this.flags = flags
    }

    override fun visitProperty(flags: Flags, name: String, getterFlags: Flags, setterFlags: Flags): KmPropertyVisitor =
        KmProperty(flags, name, getterFlags, setterFlags).apply { properties.add(this) }

    override fun visitConstructor(flags: Flags): KmConstructorVisitor =
        KmConstructor(flags).apply { constructors.add(this) }

    override fun visitCompanionObject(name: String) {
        this.companionObject = name
    }

    override fun visitSealedSubclass(name: ClassName) {
        sealedSubclasses.add(name)
    }

    override fun visitInlineClassUnderlyingType(flags: Flags): KmTypeVisitor =
        KmType(flags).also { inlineClassUnderlyingType = it }
}

// Jackson Metadata Class
internal class JmClass(
    clazz: Class<*>,
    kmClass: ReducedKmClass,
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
    val propertyNameSet: Set<String> get() = allPropsMap.keys
    val properties: List<KmProperty> = allPropsMap.values.toList()
    val sealedSubclasses: List<ClassName> = kmClass.sealedSubclasses
    private val companionPropName: String? = kmClass.companionObject
    val companion: CompanionObject? by lazy { companionPropName?.let { CompanionObject(clazz, it) } }
    val inlineClassUnderlyingType: KmType? = kmClass.inlineClassUnderlyingType

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

    fun findPropertyByGetter(getter: Method): KmProperty? {
        val getterName = getter.name
        return properties.find { it.getterSignature?.name == getterName }
    }

    internal class CompanionObject(declaringClass: Class<*>, companionObject: String) {
        private class ReducedCompanionVisitor : ReducedKmClassVisitor() {
            val functions: MutableList<KmFunction> = arrayListOf()

            override fun visitFunction(flags: Flags, name: String): KmFunctionVisitor? = KmFunction(flags, name)
                .apply { functions.add(this) }

            companion object {
                fun from(companionClass: Class<*>): ReducedCompanionVisitor = ReducedCompanionVisitor().apply {
                    companionClass.getAnnotation(Metadata::class.java)!!.accept(this)
                }
            }
        }

        private val companionField: Field = declaringClass.getDeclaredField(companionObject)
        val type: Class<*> = companionField.type
        val isAccessible: Boolean = companionField.isAccessible
        private val functions by lazy { ReducedCompanionVisitor.from(type).functions }
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
