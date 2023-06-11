package io.github.projectmapk.jackson.module.kogera.deser.singleton_support

import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier
import io.github.projectmapk.jackson.module.kogera.toKmClass
import kotlinx.metadata.Flag

// [module-kotlin#225]: keep Kotlin singletons as singletons
internal object KotlinBeanDeserializerModifier : BeanDeserializerModifier() {
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

private fun objectSingletonInstance(beanClass: Class<*>): Any? = beanClass.toKmClass()?.let {
    // It is not assumed that the companion object is the target
    if (Flag.Class.IS_OBJECT(it.flags) && !Flag.Class.IS_COMPANION_OBJECT(it.flags)) {
        beanClass.getDeclaredField("INSTANCE").get(null)
    } else {
        null
    }
}
