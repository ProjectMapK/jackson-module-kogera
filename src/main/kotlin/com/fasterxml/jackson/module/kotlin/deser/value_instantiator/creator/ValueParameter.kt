package com.fasterxml.jackson.module.kotlin.deser.value_instantiator.creator

import kotlinx.metadata.Flag
import kotlinx.metadata.KmClassifier
import kotlinx.metadata.KmType
import kotlinx.metadata.KmTypeProjection
import kotlinx.metadata.KmValueParameter

internal class ValueParameter(private val param: KmValueParameter) {
    internal sealed interface Argument {
        val isNullable: Boolean
        val name: String?

        object Star : Argument {
            override val isNullable: Boolean = true
            override val name: String? = null
        }

        class ArgumentImpl(type: KmType) : Argument {
            override val isNullable: Boolean = Flag.Type.IS_NULLABLE(type.flags)

            // TODO: Formatting because it is a minimal display about the error content
            override val name: String = type.classifier.toString()
        }
    }

    val name: String = param.name
    val type: KmType = param.type
    val isOptional: Boolean = Flag.ValueParameter.DECLARES_DEFAULT_VALUE(param.flags)
    val isPrimitive: Boolean = Flag.IS_PRIVATE(param.type.flags)
    val isNullable: Boolean = Flag.Type.IS_NULLABLE(param.type.flags)
    val isGenericType: Boolean = param.type.classifier is KmClassifier.TypeParameter

    val arguments: List<Argument> by lazy {
        param.type.arguments.map {
            if (it === KmTypeProjection.STAR) {
                Argument.Star
            } else {
                // If it is not a StarProjection, type is not null
                Argument.ArgumentImpl(it.type!!)
            }
        }
    }

    // TODO: Formatting into a form that is easy to understand as an error message with reference to KParameter
    override fun toString() = "parameter name: ${param.name} parameter type: ${param.type.classifier}"
}
