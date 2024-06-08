package io.github.projectmapk.jackson.module.kogera.jmClass

import kotlinx.metadata.KmFunction
import kotlinx.metadata.jvm.JvmMethodSignature
import kotlinx.metadata.jvm.signature

internal class JmFunction(
    val signature: JvmMethodSignature?,
    val valueParameters: List<JmValueParameter>
) {
    constructor(function: KmFunction) : this(function.signature, function.valueParameters.map { JmValueParameter(it) })
}
