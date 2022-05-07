package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature.NullIsSameAsDefault
import com.fasterxml.jackson.module.kotlin.KotlinFeature.NullToEmptyCollection
import com.fasterxml.jackson.module.kotlin.KotlinFeature.NullToEmptyMap
import com.fasterxml.jackson.module.kotlin.KotlinFeature.StrictNullChecks
import com.fasterxml.jackson.module.kotlin.SingletonSupport.CANONICALIZE
import com.fasterxml.jackson.module.kotlin.SingletonSupport.DISABLED
import com.fasterxml.jackson.module.kotlin.deser.deserializers.KotlinDeserializers
import com.fasterxml.jackson.module.kotlin.deser.deserializers.KotlinKeyDeserializers
import com.fasterxml.jackson.module.kotlin.deser.singleton_support.KotlinBeanDeserializerModifier
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.KotlinInstantiators
import com.fasterxml.jackson.module.kotlin.ser.serializers.KotlinKeySerializers
import com.fasterxml.jackson.module.kotlin.ser.serializers.KotlinSerializers
import java.util.*
import kotlin.reflect.KClass

/**
 * @param   reflectionCacheSize     Default: 512.  Size, in items, of the caches used for mapping objects.
 * @param   nullToEmptyCollection   Default: false.  Whether to deserialize null values for collection properties as
 *                                      empty collections.
 * @param   nullToEmptyMap          Default: false.  Whether to deserialize null values for a map property to an empty
 *                                      map object.
 * @param   nullIsSameAsDefault     Default false.  Whether to treat null values as absent when deserializing, thereby
 *                                      using the default value provided in Kotlin.
 * @param   singletonSupport        Default: DISABLED.  Mode for singleton handling.
 *                                      See {@link com.fasterxml.jackson.module.kotlin.SingletonSupport label}
 * @param   strictNullChecks        Default: false.  Whether to check deserialized collections.  With this disabled,
 *                                      the default, collections which are typed to disallow null members
 *                                      (e.g. List<String>) may contain null values after deserialization.  Enabling it
 *                                      protects against this but has significant performance impact.
 */
class KotlinModule private constructor(
    val reflectionCacheSize: Int = 512,
    val nullToEmptyCollection: Boolean = false,
    val nullToEmptyMap: Boolean = false,
    val nullIsSameAsDefault: Boolean = false,
    val singletonSupport: SingletonSupport = DISABLED,
    val strictNullChecks: Boolean = false
) : SimpleModule(KotlinModule::class.java.name /* TODO: add Version parameter */) {
    private constructor(builder: Builder) : this(
        builder.reflectionCacheSize,
        builder.isEnabled(NullToEmptyCollection),
        builder.isEnabled(NullToEmptyMap),
        builder.isEnabled(NullIsSameAsDefault),
        when {
            builder.isEnabled(KotlinFeature.SingletonSupport) -> CANONICALIZE
            else -> DISABLED
        },
        builder.isEnabled(StrictNullChecks)
    )

    companion object {
        const val serialVersionUID = 1L
    }

    private val ignoredClassesForImplyingJsonCreator = emptySet<KClass<*>>()

    override fun setupModule(context: SetupContext) {
        super.setupModule(context)

        if (!context.isEnabled(MapperFeature.USE_ANNOTATIONS)) {
            throw IllegalStateException("The Jackson Kotlin module requires USE_ANNOTATIONS to be true or it cannot function")
        }

        val cache = ReflectionCache(reflectionCacheSize)

        context.addValueInstantiators(KotlinInstantiators(cache, nullToEmptyCollection, nullToEmptyMap, nullIsSameAsDefault, strictNullChecks))

        when (singletonSupport) {
            DISABLED -> Unit
            CANONICALIZE -> {
                context.addBeanDeserializerModifier(KotlinBeanDeserializerModifier)
            }
        }

        context.insertAnnotationIntrospector(KotlinAnnotationIntrospector(context, cache, nullToEmptyCollection, nullToEmptyMap, nullIsSameAsDefault))
        context.appendAnnotationIntrospector(KotlinNamesAnnotationIntrospector(this, cache, ignoredClassesForImplyingJsonCreator))

        context.addDeserializers(KotlinDeserializers())
        context.addKeyDeserializers(KotlinKeyDeserializers)
        context.addSerializers(KotlinSerializers())
        context.addKeySerializers(KotlinKeySerializers())

        // ranges
        context.setMixInAnnotations(ClosedRange::class.java, ClosedRangeMixin::class.java)
    }

    class Builder {
        var reflectionCacheSize: Int = 512
            private set

        private val bitSet: BitSet = KotlinFeature.defaults

        fun withReflectionCacheSize(reflectionCacheSize: Int): Builder = apply {
            this.reflectionCacheSize = reflectionCacheSize
        }

        fun enable(feature: KotlinFeature): Builder = apply {
            bitSet.or(feature.bitSet)
        }

        fun disable(feature: KotlinFeature): Builder = apply {
            bitSet.andNot(feature.bitSet)
        }

        fun configure(feature: KotlinFeature, enabled: Boolean): Builder =
            when {
                enabled -> enable(feature)
                else -> disable(feature)
            }

        fun isEnabled(feature: KotlinFeature): Boolean =
            bitSet.intersects(feature.bitSet)

        fun build(): KotlinModule =
            KotlinModule(this)
    }
}
