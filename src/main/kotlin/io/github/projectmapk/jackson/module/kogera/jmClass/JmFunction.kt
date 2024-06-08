package io.github.projectmapk.jackson.module.kogera.jmClass

import kotlinx.metadata.KmFunction
import kotlinx.metadata.KmValueParameter
import kotlinx.metadata.jvm.JvmMethodSignature
import kotlinx.metadata.jvm.signature

internal class JmFunction(
    val signature: JvmMethodSignature?,
    val valueParameters: List<KmValueParameter>
) {
    constructor(function: KmFunction) : this(function.signature, function.valueParameters)
}
