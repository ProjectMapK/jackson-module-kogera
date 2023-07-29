package io.github.projectmapk.jackson.module.kogera

import kotlinx.metadata.KmClass

// Jackson Metadata Class
internal class JmClass(val kmClass: KmClass) {
    companion object {
        fun createOrNull(clazz: Class<*>): JmClass? = clazz.toKmClass()?.let { JmClass(it) }
    }
}
