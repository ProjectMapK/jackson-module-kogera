package io.github.projectmapk.jackson.module.kogera

import kotlinx.metadata.KmClass
import kotlinx.metadata.KmProperty

// Jackson Metadata Class
internal class JmClass(val kmClass: KmClass) {
    val properties: List<KmProperty> = kmClass.properties

    companion object {
        fun createOrNull(clazz: Class<*>): JmClass? = clazz.toKmClass()?.let { JmClass(it) }
    }
}
