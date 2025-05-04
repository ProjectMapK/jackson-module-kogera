package io.github.projectmapk.jackson.module.kogera.jmClass

import io.github.projectmapk.jackson.module.kogera.toDescBuilder
import java.lang.reflect.Constructor
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

    // Only constructors that take a value class as an argument have a DefaultConstructorMarker on the Signature.
    private fun StringBuilder.valueDesc(
        len: Int
    ) = replace(len - 1, len, "Lkotlin/jvm/internal/DefaultConstructorMarker;)V").toString()

    fun isMetadataFor(constructor: Constructor<*>): Boolean {
        val targetDesc = signature?.descriptor

        val descHead = constructor.parameterTypes.toDescBuilder()
        val len = descHead.length
        val desc = CharArray(len + 1).apply {
            descHead.getChars(0, len, this, 0)
            this[len] = 'V'
        }.let { String(it) }

        // Constructors always have the same name, so only desc is compared
        return targetDesc == desc || targetDesc == descHead.valueDesc(len)
    }
}
