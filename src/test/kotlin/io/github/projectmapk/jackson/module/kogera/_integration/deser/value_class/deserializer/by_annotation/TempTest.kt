package io.github.projectmapk.jackson.module.kogera._integration.deser.value_class.deserializer.by_annotation

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.github.projectmapk.jackson.module.kogera._integration.deser.value_class.Primitive
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// Only a temporary test is implemented because the problem that
// KNAI.findDeserializationConverter does not work when Deserializer is specified in the annotation,
// resulting in an IllegalArgumentException, has not been resolved.
class TempTest {
    data class Dst(@JsonDeserialize(using = Primitive.Deserializer::class) val value: Primitive?)

    @Test
    fun test() {
        val result = jacksonObjectMapper().readValue<Dst>("""{"value":1}""")
        assertEquals(Dst(Primitive(101)), result)
    }
}
