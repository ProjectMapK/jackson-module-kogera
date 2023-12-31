package io.github.projectmapk.jackson.module.kogera

import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription
import com.fasterxml.jackson.databind.introspect.BasicClassIntrospector
import com.fasterxml.jackson.databind.introspect.POJOPropertiesCollector

// If the constructor has value class parameter,
// annotations given to synthetic constructor parameter is injected into that constructor parameter.
private fun AnnotatedConstructor.injectSyntheticAnnotations() {
    val constructor = annotated

    @Suppress("UNCHECKED_CAST")
    val syntheticParams = constructor.parameterTypes.copyOf(constructor.parameterCount + 1)
        .apply { this[constructor.parameterCount] = defaultConstructorMarker } as Array<Class<*>>
    // Try to get syntheticConstructor, and if not, do nothing.
    val syntheticConstructor = runCatching { declaringClass.getDeclaredConstructorBy(syntheticParams) }
        .getOrNull()
        ?: return

    (0 until constructor.parameterCount).forEach { i ->
        val map = getParameterAnnotations(i)
        syntheticConstructor.parameterAnnotations[i].forEach { map.add(it) }
    }
}

internal class KotlinBeanDescription(coll: POJOPropertiesCollector) : BasicBeanDescription(coll) {
    init {
        this.constructors?.forEach { it.injectSyntheticAnnotations() }
    }
}

// Almost all copies of super @2.14.1
internal object KotlinClassIntrospector : BasicClassIntrospector() {
    private fun readResolve(): Any = KotlinClassIntrospector

    override fun forSerialization(
        config: SerializationConfig,
        type: JavaType,
        r: MixInResolver
    ): BasicBeanDescription {
        // minor optimization: for some JDK types do minimal introspection
        return _findStdTypeDesc(config, type)
            // As per [databind#550], skip full introspection for some of standard
            // structured types as well
            ?: _findStdJdkCollectionDesc(config, type)
            ?: run {
                val coll = collectProperties(config, type, r, true)

                if (type.rawClass.isAnnotationPresent(Metadata::class.java)) {
                    KotlinBeanDescription(coll)
                } else {
                    BasicBeanDescription.forDeserialization(coll)
                }
            }
    }

    override fun forDeserialization(
        config: DeserializationConfig,
        type: JavaType,
        r: MixInResolver
    ): BasicBeanDescription {
        // minor optimization: for some JDK types do minimal introspection
        return _findStdTypeDesc(config, type)
            // As per [Databind#550], skip full introspection for some of standard
            // structured types as well
            ?: _findStdJdkCollectionDesc(config, type)
            ?: run {
                val coll = collectProperties(config, type, r, false)

                if (type.rawClass.isAnnotationPresent(Metadata::class.java)) {
                    KotlinBeanDescription(coll)
                } else {
                    BasicBeanDescription.forDeserialization(coll)
                }
            }
    }
}
