package io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.deserializer.byAnnotation

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.github.projectmapk.jackson.module.kogera.KotlinFeature
import io.github.projectmapk.jackson.module.kogera.KotlinModule
import io.github.projectmapk.jackson.module.kogera.readValue
import io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass.Primitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// Only a temporary test is implemented because the problem that
// KNAI.findDeserializationConverter does not work when Deserializer is specified in the annotation,
// resulting in an IllegalArgumentException, has not been resolved.
class TempTest {
    data class Dst(@JsonDeserialize(using = Primitive.Deserializer::class) val value: Primitive?)

    @Test
    fun test() {
        val result = KotlinModule.Builder()
            .enable(KotlinFeature.CopySyntheticConstructorParameterAnnotations)
            .build()
            .let { ObjectMapper().registerModule(it) }
            .readValue<Dst>("""{"value":1}""")

        assertEquals(Dst(Primitive(101)), result)
    }
}
