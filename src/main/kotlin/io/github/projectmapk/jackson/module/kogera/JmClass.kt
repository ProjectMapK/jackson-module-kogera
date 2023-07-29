package io.github.projectmapk.jackson.module.kogera

import kotlinx.metadata.KmClass
import kotlinx.metadata.KmProperty
import kotlinx.metadata.jvm.getterSignature
import java.lang.reflect.Method

// Jackson Metadata Class
internal class JmClass(val kmClass: KmClass) {
    val properties: List<KmProperty> = kmClass.properties

    fun findPropertyByGetter(getter: Method): KmProperty? {
        val signature = getter.toSignature()
        return properties.find { it.getterSignature == signature }
    }

    companion object {
        fun createOrNull(clazz: Class<*>): JmClass? = clazz.toKmClass()?.let { JmClass(it) }
    }
}
