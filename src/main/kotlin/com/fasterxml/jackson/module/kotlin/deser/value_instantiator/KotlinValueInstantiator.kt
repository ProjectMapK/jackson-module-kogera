package com.fasterxml.jackson.module.kotlin.deser.value_instantiator

import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.deser.SettableBeanProperty
import com.fasterxml.jackson.databind.deser.ValueInstantiator
import com.fasterxml.jackson.databind.deser.ValueInstantiators
import com.fasterxml.jackson.databind.deser.impl.NullsAsEmptyProvider
import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer
import com.fasterxml.jackson.databind.deser.std.StdValueInstantiator
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.fasterxml.jackson.module.kotlin.ReflectionCache
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.creator.ValueCreator
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.creator.ValueParameter
import com.fasterxml.jackson.module.kotlin.isKotlinClass
import com.fasterxml.jackson.module.kotlin.wrapWithPath

internal class KotlinValueInstantiator(
    src: StdValueInstantiator,
    private val cache: ReflectionCache,
    private val nullToEmptyCollection: Boolean,
    private val nullToEmptyMap: Boolean,
    private val nullIsSameAsDefault: Boolean,
    private val strictNullChecks: Boolean
) : StdValueInstantiator(src) {
    // If the collection type argument cannot be obtained, treat it as nullable
    // @see com.fasterxml.jackson.module.kotlin._ported.test.StrictNullChecksTest#testListOfGenericWithNullValue
    private fun ValueParameter.isNullishTypeAt(index: Int) = arguments.getOrNull(index)?.isNullable ?: true

    private fun JavaType.requireEmptyValue() =
        (nullToEmptyCollection && this.isCollectionLikeType) || (nullToEmptyMap && this.isMapLikeType)

    private fun strictNullCheck(
        ctxt: DeserializationContext,
        jsonProp: SettableBeanProperty,
        paramDef: ValueParameter,
        paramVal: Any
    ) {
        // If an error occurs, Argument.name is always non-null
        // @see com.fasterxml.jackson.module.kotlin.deser.value_instantiator.creator.Argument
        when {
            paramVal is Collection<*> && !paramDef.isNullishTypeAt(0) && paramVal.any { it == null } ->
                "collection" to paramDef.arguments[0].name!!
            paramVal is Array<*> && !paramDef.isNullishTypeAt(0) && paramVal.any { it == null } ->
                "array" to paramDef.arguments[0].name!!
            paramVal is Map<*, *> && !paramDef.isNullishTypeAt(1) && paramVal.values.any { it == null } ->
                "map" to paramDef.arguments[1].name!!
            else -> null
        }?.let { (paramType, itemType) ->
            throw MissingKotlinParameterException(
                parameter = paramDef,
                processor = ctxt.parser,
                msg = "Instantiation of $itemType $paramType failed for JSON property ${jsonProp.name} due to null value in a $paramType that does not allow null values"
            ).wrapWithPath(this.valueClass, jsonProp.name)
        }
    }

    override fun createFromObjectWith(
        ctxt: DeserializationContext,
        props: Array<out SettableBeanProperty>,
        buffer: PropertyValueBuffer
    ): Any? {
        val valueCreator: ValueCreator<*> = cache.valueCreatorFromJava(_withArgsCreator)
            ?: return super.createFromObjectWith(ctxt, props, buffer)

        val bucket = valueCreator.generateBucket()

        valueCreator.valueParameters.forEachIndexed { idx, paramDef ->
            val jsonProp = props[idx]
            val isMissing = !buffer.hasParameter(jsonProp)

            if (isMissing && paramDef.isOptional) {
                return@forEachIndexed
            }

            var paramVal = if (!isMissing || paramDef.isPrimitive || jsonProp.hasInjectableValueId()) {
                val tempParamVal = buffer.getParameter(jsonProp)
                if (nullIsSameAsDefault && tempParamVal == null && paramDef.isOptional) {
                    return@forEachIndexed
                }
                tempParamVal
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
                    if (isMissingAndRequired || (!paramDef.isGenericType && !paramDef.isNullable)) {
                        throw MissingKotlinParameterException(
                            parameter = paramDef,
                            processor = ctxt.parser,
                            msg = "Instantiation of $valueTypeDesc value failed for JSON property ${jsonProp.name} " +
                                "due to missing (therefore NULL) value for creator parameter ${paramDef.name} " +
                                "which is a non-nullable type"
                        ).wrapWithPath(this.valueClass, jsonProp.name)
                    }
                }
            } else {
                if (strictNullChecks) strictNullCheck(ctxt, jsonProp, paramDef, paramVal)
            }

            bucket[idx] = paramVal
        }

        valueCreator.checkAccessibility(ctxt)
        return valueCreator.callBy(bucket)
    }

    private fun SettableBeanProperty.hasInjectableValueId(): Boolean = injectableValueId != null
}

internal class KotlinInstantiators(
    private val cache: ReflectionCache,
    private val nullToEmptyCollection: Boolean,
    private val nullToEmptyMap: Boolean,
    private val nullIsSameAsDefault: Boolean,
    private val strictNullChecks: Boolean
) : ValueInstantiators {
    override fun findValueInstantiator(
        deserConfig: DeserializationConfig,
        beanDescriptor: BeanDescription,
        defaultInstantiator: ValueInstantiator
    ): ValueInstantiator {
        return if (beanDescriptor.beanClass.isKotlinClass()) {
            if (defaultInstantiator::class == StdValueInstantiator::class) {
                KotlinValueInstantiator(
                    defaultInstantiator as StdValueInstantiator,
                    cache,
                    nullToEmptyCollection,
                    nullToEmptyMap,
                    nullIsSameAsDefault,
                    strictNullChecks
                )
            } else {
                // TODO: return defaultInstantiator and let default method parameters and nullability go unused?  or die with exception:
                throw IllegalStateException("KotlinValueInstantiator requires that the default ValueInstantiator is StdValueInstantiator")
            }
        } else {
            defaultInstantiator
        }
    }
}
