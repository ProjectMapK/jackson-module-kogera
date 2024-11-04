package io.github.projectmapk.jackson.module.kogera.jmClass

import kotlin.metadata.KmConstructor
import kotlin.metadata.isSecondary
import kotlin.metadata.jvm.JvmMethodSignature
import kotlin.metadata.jvm.signature

internal data class JmConstructor(
    val isSecondary: Boolean,
    val signature: JvmMethodSignature?,
    val valueParameters: List<JmValueParameter>
) {
    constructor(constructor: KmConstructor) : this(
        isSecondary = constructor.isSecondary,
        signature = constructor.signature,
        valueParameters = constructor.valueParameters.map { JmValueParameter(it) }
    )
}
