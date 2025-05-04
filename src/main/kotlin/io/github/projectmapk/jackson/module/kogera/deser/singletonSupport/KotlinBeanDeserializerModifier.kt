package io.github.projectmapk.jackson.module.kogera.deser.singletonSupport

import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier
import io.github.projectmapk.jackson.module.kogera.ReflectionCache
import java.io.Serializable
import kotlin.metadata.ClassKind

// [module-kotlin#225]: keep Kotlin singletons as singletons
internal class KotlinBeanDeserializerModifier(
    private val cache: ReflectionCache
) : BeanDeserializerModifier(),
    Serializable {
    companion object {
        // Increment is required when properties that use LRUMap are changed.
        private const val serialVersionUID = 1L
    }

    private fun objectSingletonInstance(beanClass: Class<*>): Any? = cache.getJmClass(beanClass)?.let {
        // It is not assumed that the companion object is the target
        if (it.kind == ClassKind.OBJECT) {
            beanClass.getDeclaredField("INSTANCE").get(null)
        } else {
            null
        }
    }

    override fun modifyDeserializer(
        config: DeserializationConfig,
        beanDesc: BeanDescription,
        deserializer: JsonDeserializer<*>
    ): JsonDeserializer<out Any> {
        val modifiedFromParent = super.modifyDeserializer(config, beanDesc, deserializer)

        return objectSingletonInstance(beanDesc.beanClass)
            ?.let { KotlinObjectSingletonDeserializer(it, modifiedFromParent) }
            ?: modifiedFromParent
    }
}
