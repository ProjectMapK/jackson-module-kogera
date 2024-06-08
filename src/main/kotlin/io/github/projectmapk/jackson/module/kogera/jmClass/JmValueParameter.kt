package io.github.projectmapk.jackson.module.kogera.jmClass

import io.github.projectmapk.jackson.module.kogera.reconstructClassOrNull
import kotlinx.metadata.KmClassifier
import kotlinx.metadata.KmType
import kotlinx.metadata.KmTypeProjection
import kotlinx.metadata.KmValueParameter
import kotlinx.metadata.declaresDefaultValue
import kotlinx.metadata.isNullable

internal class JmValueParameter(
    val name: String,
    val isOptional: Boolean,
    val isVararg: Boolean,
    type: KmType,
    classifier: KmClassifier.Class?
) {
    constructor(valueParameter: KmValueParameter) : this(
        valueParameter.name,
        isOptional = valueParameter.declaresDefaultValue,
        isVararg = valueParameter.varargElementType != null,
        valueParameter.type,
        valueParameter.type.classifier as? KmClassifier.Class
    )

    val isNullable: Boolean = type.isNullable
    val isGenericType: Boolean = type.classifier is KmClassifier.TypeParameter
    val arguments: List<KmTypeProjection> = type.arguments

    val isString: Boolean = classifier?.name == "kotlin/String"
    val reconstructedClassOrNull: Class<*>? by lazy {
        classifier?.reconstructClassOrNull()
    }
}
