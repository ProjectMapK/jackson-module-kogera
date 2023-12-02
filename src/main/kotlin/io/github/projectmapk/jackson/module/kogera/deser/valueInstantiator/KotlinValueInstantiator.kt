package io.github.projectmapk.jackson.module.kogera.deser.valueInstantiator

import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.deser.SettableBeanProperty
import com.fasterxml.jackson.databind.deser.ValueInstantiator
import com.fasterxml.jackson.databind.deser.impl.NullsAsEmptyProvider
import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer
import com.fasterxml.jackson.databind.deser.std.StdValueInstantiator
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.module.SimpleValueInstantiators
import io.github.projectmapk.jackson.module.kogera.ReflectionCache
import io.github.projectmapk.jackson.module.kogera.deser.valueInstantiator.creator.ConstructorValueCreator
import io.github.projectmapk.jackson.module.kogera.deser.valueInstantiator.creator.MethodValueCreator
import io.github.projectmapk.jackson.module.kogera.deser.valueInstantiator.creator.ValueCreator
import java.lang.reflect.Constructor
import java.lang.reflect.Executable
import java.lang.reflect.Method

private fun JsonMappingException.wrapWithPath(refFrom: Any?, refFieldName: String) =
    JsonMappingException.wrapWithPath(this, refFrom, refFieldName)

internal class KotlinValueInstantiator(
    src: StdValueInstantiator,
    private val cache: ReflectionCache,
    private val nullToEmptyCollection: Boolean,
    private val nullToEmptyMap: Boolean,
    private val nullIsSameAsDefault: Boolean
) : StdValueInstantiator(src) {
    private fun JavaType.requireEmptyValue() =
        (nullToEmptyCollection && this.isCollectionLikeType) || (nullToEmptyMap && this.isMapLikeType)

    private fun SettableBeanProperty.hasInjectableValueId(): Boolean = injectableValueId != null

    private fun SettableBeanProperty.skipNulls(): Boolean =
        nullIsSameAsDefault || (metadata.valueNulls == Nulls.SKIP)

    private val valueCreator: ValueCreator<*>? by ReflectProperties.lazySoft {
        val creator = _withArgsCreator.annotated as Executable
        val jmClass = cache.getJmClass(creator.declaringClass) ?: return@lazySoft null

        when (creator) {
            is Constructor<*> -> ConstructorValueCreator(creator, jmClass, cache)
            is Method -> MethodValueCreator<Any?>(creator, jmClass, cache)
            else -> throw IllegalStateException(
                "Expected a constructor or method to create a Kotlin object, instead found ${creator.javaClass.name}"
            )
        }
    } // we cannot reflect this method so do the default Java-ish behavior

    override fun createFromObjectWith(
        ctxt: DeserializationContext,
        props: Array<out SettableBeanProperty>,
        buffer: PropertyValueBuffer
    ): Any? {
        val valueCreator: ValueCreator<*> = valueCreator ?: return super.createFromObjectWith(ctxt, props, buffer)
        valueCreator.checkAccessibility(ctxt)

        val bucket = valueCreator.generateBucket()

        valueCreator.valueParameters.forEachIndexed { idx, paramDef ->
            val jsonProp = props[idx]
            val isMissing = !buffer.hasParameter(jsonProp)

            if (isMissing && paramDef.isOptional) {
                return@forEachIndexed
            }

            var paramVal = if (!isMissing || jsonProp.hasInjectableValueId()) {
                buffer.getParameter(jsonProp).apply {
                    if (this == null && jsonProp.skipNulls() && paramDef.isOptional) return@forEachIndexed
                }
            } else {
                if (paramDef.isNullable) {
                    // do not try to create any object if it is nullable and the value is missing
                    null
                } else {
                    // to get suitable "missing" value provided by deserializer
                    jsonProp.valueDeserializer?.getAbsentValue(ctxt)
                }
            }

            if (paramVal == null) {
                if (jsonProp.type.requireEmptyValue()) {
                    paramVal = NullsAsEmptyProvider(jsonProp.valueDeserializer).getNullValue(ctxt)
                } else {
                    val isMissingAndRequired = isMissing && jsonProp.isRequired
                    if (isMissingAndRequired || !(paramDef.isNullable || paramDef.isGenericType)) {
                        throw MismatchedInputException.from(
                            ctxt.parser,
                            jsonProp.type,
                            "Instantiation of $valueTypeDesc value failed for JSON property ${jsonProp.name} " +
                                "due to missing (therefore NULL) value for creator parameter ${paramDef.name} " +
                                "which is a non-nullable type"
                        ).wrapWithPath(this.valueClass, jsonProp.name)
                    }
                }
            }

            bucket[idx] = paramVal
        }

        return valueCreator.callBy(bucket)
    }
}

internal class KotlinInstantiators(
    private val cache: ReflectionCache,
    private val nullToEmptyCollection: Boolean,
    private val nullToEmptyMap: Boolean,
    private val nullIsSameAsDefault: Boolean
) : SimpleValueInstantiators() {
    override fun findValueInstantiator(
        deserConfig: DeserializationConfig,
        beanDescriptor: BeanDescription,
        defaultInstantiator: ValueInstantiator
    ): ValueInstantiator = if (cache.getJmClass(beanDescriptor.beanClass) != null) {
        if (defaultInstantiator::class == StdValueInstantiator::class) {
            KotlinValueInstantiator(
                defaultInstantiator as StdValueInstantiator,
                cache,
                nullToEmptyCollection,
                nullToEmptyMap,
                nullIsSameAsDefault
            )
        } else {
            // TODO: return defaultInstantiator and let default method parameters and nullability go unused?
            //       or die with exception:
            throw IllegalStateException(
                "KotlinValueInstantiator requires that the default ValueInstantiator is StdValueInstantiator"
            )
        }
    } else {
        defaultInstantiator
    }
}
