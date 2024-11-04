package io.github.projectmapk.jackson.module.kogera.jmClass

import kotlin.metadata.KmProperty
import kotlin.metadata.KmType
import kotlin.metadata.jvm.JvmMemberSignature
import kotlin.metadata.jvm.getterSignature
import kotlin.metadata.jvm.setterSignature

internal data class JmProperty(
    val name: String?,
    val getterName: String?,
    val setterSignature: JvmMemberSignature?,
    val returnType: KmType
) {
    constructor(kmProperty: KmProperty) : this(
        name = kmProperty.name,
        getterName = kmProperty.getterSignature?.name,
        setterSignature = kmProperty.setterSignature,
        returnType = kmProperty.returnType
    )
}
