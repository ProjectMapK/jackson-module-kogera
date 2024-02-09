package io.github.projectmapk.jackson.module.kogera

import kotlinx.metadata.ClassKind
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
import kotlinx.metadata.internal.accept
import kotlinx.metadata.internal.metadata.jvm.deserialization.JvmProtoBufUtil
import kotlinx.metadata.jvm.getterSignature
import kotlinx.metadata.jvm.signature
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlinx.metadata.internal.metadata.deserialization.Flags as ProtoFlags

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

// Jackson Metadata Class
internal sealed interface JmClass {
    class CompanionObject(declaringClass: Class<*>, companionObject: String) {
        private class ReducedCompanionVisitor(companionClass: Class<*>) : ReducedKmClassVisitor() {
            val functions: MutableList<KmFunction> = arrayListOf()

            init {
                companionClass.getAnnotation(Metadata::class.java)!!.accept(this)
            }

            override fun visitFunction(flags: Flags, name: String): KmFunctionVisitor = KmFunction(flags, name)
                .apply { functions.add(this) }
        }

        private val companionField: Field = declaringClass.getDeclaredField(companionObject)
        val type: Class<*> = companionField.type
        val isAccessible: Boolean = companionField.isAccessible
        private val functions by lazy { ReducedCompanionVisitor(type).functions }
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
    metadata: Metadata,
    superJmClass: JmClass?,
    interfaceJmClasses: List<JmClass>
) : ReducedKmClassVisitor(), JmClass {
    private val allPropsMap: MutableMap<String, KmProperty> = mutableMapOf()

    // Defined as non-lazy because it is always read in both serialization and deserialization
    override val properties: List<KmProperty>

    private var companionPropName: String? = null
    override lateinit var kind: ClassKind
    override val constructors: MutableList<KmConstructor> = mutableListOf()
    override val sealedSubclasses: MutableList<ClassName> = mutableListOf()
    override var inlineClassUnderlyingType: KmType? = null

    init {
        metadata.accept(this)

        // Add properties of inherited classes and interfaces
        // If an `interface` is implicitly implemented by an abstract class,
        // it is necessary to obtain a more specific type, so always add it from the abstract class first.
        (superJmClass as JmClassImpl?)?.allPropsMap?.forEach {
            this.allPropsMap.putIfAbsent(it.key, it.value)
        }
        @Suppress("UNCHECKED_CAST")
        (interfaceJmClasses as List<JmClassImpl>).forEach { i ->
            i.allPropsMap.forEach {
                this.allPropsMap.putIfAbsent(it.key, it.value)
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

    // KmClassVisitor
    override fun visit(flags: Flags, name: ClassName) {
        kind = ClassKind.values()[ProtoFlags.CLASS_KIND.get(flags).number]
    }

    override fun visitProperty(flags: Flags, name: String, getterFlags: Flags, setterFlags: Flags): KmPropertyVisitor =
        KmProperty(flags, name, getterFlags, setterFlags).apply { allPropsMap[name] = this }

    override fun visitConstructor(flags: Flags): KmConstructorVisitor =
        KmConstructor(flags).apply { constructors.add(this) }

    override fun visitCompanionObject(name: String) {
        this.companionPropName = name
    }

    override fun visitSealedSubclass(name: ClassName) {
        sealedSubclasses.add(name)
    }

    override fun visitInlineClassUnderlyingType(flags: Flags): KmTypeVisitor =
        KmType(flags).also { inlineClassUnderlyingType = it }
}

private fun Metadata.accept(visitor: ReducedKmClassVisitor) {
    val (strings, proto) = JvmProtoBufUtil.readClassDataFrom(data1.takeIf(Array<*>::isNotEmpty)!!, data2)
    proto.accept(visitor, strings)
}

internal fun JmClass(
    clazz: Class<*>,
    metadata: Metadata,
    superJmClass: JmClass?,
    interfaceJmClasses: List<JmClass>
): JmClass = JmClassImpl(clazz, metadata, superJmClass, interfaceJmClasses)
