package io.github.projectmapk.jackson.module.kogera.jmClass

import kotlinx.metadata.KmConstructor
import kotlinx.metadata.KmValueParameter
import kotlinx.metadata.isSecondary
import kotlinx.metadata.jvm.JvmMethodSignature
import kotlinx.metadata.jvm.signature

internal data class JmConstructor(
    val isSecondary: Boolean,
    val signature: JvmMethodSignature?,
    val valueParameters: List<KmValueParameter>
) {
    constructor(constructor: KmConstructor) : this(
        isSecondary = constructor.isSecondary,
        signature = constructor.signature,
        valueParameters = constructor.valueParameters
    )
}
