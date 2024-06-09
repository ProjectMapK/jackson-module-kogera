package io.github.projectmapk.jackson.module.kogera.jmClass

import kotlinx.metadata.KmProperty
import kotlinx.metadata.KmType
import kotlinx.metadata.jvm.JvmMemberSignature
import kotlinx.metadata.jvm.getterSignature
import kotlinx.metadata.jvm.setterSignature

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
