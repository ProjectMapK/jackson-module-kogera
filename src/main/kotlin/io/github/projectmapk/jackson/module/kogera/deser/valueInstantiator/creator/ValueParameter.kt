package io.github.projectmapk.jackson.module.kogera.deser.valueInstantiator.creator

import io.github.projectmapk.jackson.module.kogera.isNullable
import io.github.projectmapk.jackson.module.kogera.reconstructClassOrNull
import kotlinx.metadata.KmClassifier
import kotlinx.metadata.KmType
import kotlinx.metadata.KmValueParameter
import kotlinx.metadata.declaresDefaultValue

internal class ValueParameter(param: KmValueParameter) {
    val name: String = param.name
    val type: KmType = param.type
    val isOptional: Boolean = param.declaresDefaultValue
    val isPrimitive: Boolean by lazy { type.reconstructClassOrNull()?.isPrimitive == true }
    val isNullable: Boolean = type.isNullable()
    val isGenericType: Boolean = type.classifier is KmClassifier.TypeParameter

    // TODO: Formatting into a form that is easy to understand as an error message with reference to KParameter
    override fun toString() = "parameter name: $name parameter type: ${type.classifier}"
}
