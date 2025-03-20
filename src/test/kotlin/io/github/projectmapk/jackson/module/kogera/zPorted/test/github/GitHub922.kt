package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.projectmapk.jackson.module.kogera.KotlinFeature
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GitHub922 {
    private inline fun <reified T : Any> ObjectMapper.introspectSerialization(): BeanDescription =
        serializationConfig.introspect(serializationConfig.constructType(T::class.java))

    private inline fun <reified T : Any> ObjectMapper.introspectDeserialization(): BeanDescription =
        deserializationConfig.introspect(deserializationConfig.constructType(T::class.java))

    private fun BeanDescription.isRequired(propertyName: String): Boolean =
        this.findProperties().first { it.name == propertyName }.isRequired

    @Test
    fun `nullToEmpty does not override specification by Java annotation`() {
        val mapper = jacksonObjectMapper {
            enable(KotlinFeature.NullToEmptyCollection)
            enable(KotlinFeature.NullToEmptyMap)
        }

        val desc = mapper.introspectDeserialization<GitHub922RequiredCollectionsDtoJava>()

        assertTrue(desc.isRequired("list"))
        assertTrue(desc.isRequired("map"))
    }
}
