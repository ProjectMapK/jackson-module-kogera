package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.OptBoolean
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.projectmapk.jackson.module.kogera.KotlinFeature
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.reflect.full.memberProperties

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

    // isRequired_required_nullability_expected
    @Suppress("PropertyName")
    data class IsRequiredDto(
        // region: isRequired takes precedence
        @JsonProperty(isRequired = OptBoolean.FALSE, required = false)
        val FALSE_false_nullable_false: String?,
        @JsonProperty(isRequired = OptBoolean.FALSE, required = false)
        val FALSE_false_nonNull_false: String,
        @JsonProperty(isRequired = OptBoolean.FALSE, required = true)
        val FALSE_true_nullable_false: String?,
        @JsonProperty(isRequired = OptBoolean.FALSE, required = true)
        val FALSE_true_nonNull_false: String,
        @JsonProperty(isRequired = OptBoolean.TRUE, required = false)
        val TRUE_false_nullable_true: String?,
        @JsonProperty(isRequired = OptBoolean.TRUE, required = false)
        val TRUE_false_nonNull_true: String,
        @JsonProperty(isRequired = OptBoolean.TRUE, required = true)
        val TRUE_true_nullable_true: String?,
        @JsonProperty(isRequired = OptBoolean.TRUE, required = true)
        val TRUE_true_nonNull_true: String,
        // endregion
        // region: If isRequired is the default, only overrides by required = true will work.
        @JsonProperty(isRequired = OptBoolean.DEFAULT, required = false)
        val DEFAULT_false_nullable_false: String?,
        @JsonProperty(isRequired = OptBoolean.DEFAULT, required = false)
        val DEFAULT_false_nonNull_true: String,
        @JsonProperty(isRequired = OptBoolean.DEFAULT, required = true)
        val DEFAULT_true_nullable_true: String?,
        @JsonProperty(isRequired = OptBoolean.DEFAULT, required = true)
        val DEFAULT_true_nonNull_true: String,
        // endregion
    )

    @Test
    fun `JsonProperty properly overrides required`() {
        val desc = defaultMapper.introspectDeserialization<IsRequiredDto>()

        IsRequiredDto::class.memberProperties.forEach { prop ->
            val name = prop.name
            val expected = name.split("_").last().toBoolean()

            assertEquals(expected, desc.isRequired(name), name)
        }
    }
}
