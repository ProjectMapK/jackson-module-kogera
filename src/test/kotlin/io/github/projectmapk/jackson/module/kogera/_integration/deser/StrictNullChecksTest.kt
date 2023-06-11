package io.github.projectmapk.jackson.module.kogera._integration.deser

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import io.github.projectmapk.jackson.module.kogera.KotlinFeature
import io.github.projectmapk.jackson.module.kogera.KotlinModule
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class StrictNullChecksTest {
    val mapper: ObjectMapper = ObjectMapper()
        .registerModule(
            KotlinModule.Builder()
                .enable(KotlinFeature.StrictNullChecks)
                .build()
        )

    class ArrayWrapper(val value: Array<Int>)
    data class ListWrapper(val value: List<Int>)
    data class MapWrapper(val value: Map<String, Int>)

    @Nested
    inner class NonNullInput {
        @Test
        fun array() {
            val expected = ArrayWrapper(arrayOf(1))
            val src = mapper.writeValueAsString(expected)
            val result = mapper.readValue<ArrayWrapper>(src)

            assertArrayEquals(expected.value, result.value)
        }

        @Test
        fun list() {
            val expected = ListWrapper(listOf(1))
            val src = mapper.writeValueAsString(expected)
            val result = mapper.readValue<ListWrapper>(src)

            assertEquals(expected, result)
        }

        @Test
        fun map() {
            val expected = MapWrapper(mapOf("foo" to 1))
            val src = mapper.writeValueAsString(expected)
            val result = mapper.readValue<MapWrapper>(src)

            assertEquals(expected, result)
        }
    }

    data class AnyWrapper(val value: Any)

    @Nested
    inner class NullInput {
        @Test
        fun array() {
            val src = mapper.writeValueAsString(AnyWrapper(arrayOf<Int?>(null)))
            assertThrows<MismatchedInputException> { mapper.readValue<ArrayWrapper>(src) }
        }

        @Test
        fun list() {
            val src = mapper.writeValueAsString(AnyWrapper(arrayOf<Int?>(null)))
            assertThrows<MismatchedInputException> { mapper.readValue<ListWrapper>(src) }
        }

        @Test
        fun map() {
            val src = mapper.writeValueAsString(AnyWrapper(mapOf("foo" to null)))
            assertThrows<MismatchedInputException> { mapper.readValue<MapWrapper>(src) }
        }
    }
}
