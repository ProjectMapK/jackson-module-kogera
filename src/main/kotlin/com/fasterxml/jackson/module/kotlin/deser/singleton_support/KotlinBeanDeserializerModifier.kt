package com.fasterxml.jackson.module.kotlin.deser.singleton_support

import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier
import com.fasterxml.jackson.module.kotlin.isKotlinClass

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

private fun objectSingletonInstance(beanClass: Class<*>): Any? = beanClass.takeIf { beanClass.isKotlinClass() }
    ?.let { it.kotlin.objectInstance }
