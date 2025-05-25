package io.github.projectmapk.jackson.module.kogera.jmClass

import kotlin.metadata.KmFunction
import kotlin.metadata.jvm.JvmMethodSignature
import kotlin.metadata.jvm.signature

internal class JmFunction(
    val signature: JvmMethodSignature?,
    val valueParameters: List<JmValueParameter>,
) {
    constructor(function: KmFunction) : this(function.signature, function.valueParameters.map { JmValueParameter(it) })
}
