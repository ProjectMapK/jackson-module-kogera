package io.github.projectmapk.jackson.module.kogera.deser.value_instantiator.creator

import io.github.projectmapk.jackson.module.kogera.isNullable
import kotlinx.metadata.Flag
import kotlinx.metadata.KmClassifier
import kotlinx.metadata.KmType
import kotlinx.metadata.KmValueParameter

internal class ValueParameter(param: KmValueParameter) {
    val name: String = param.name
    val type: KmType = param.type
    val isOptional: Boolean = Flag.ValueParameter.DECLARES_DEFAULT_VALUE(param.flags)
    val isPrimitive: Boolean = Flag.IS_PRIVATE(param.type.flags)
    val isNullable: Boolean = type.isNullable()
    val isGenericType: Boolean = param.type.classifier is KmClassifier.TypeParameter

    // TODO: Formatting into a form that is easy to understand as an error message with reference to KParameter
    override fun toString() = "parameter name: $name parameter type: ${type.classifier}"
}
