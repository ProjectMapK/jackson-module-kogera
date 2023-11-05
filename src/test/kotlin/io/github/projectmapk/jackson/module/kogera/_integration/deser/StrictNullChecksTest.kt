package io.github.projectmapk.jackson.module.kogera._integration.deser

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.InvalidNullException
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
            assertThrows<InvalidNullException> { mapper.readValue<ArrayWrapper>(src) }
        }

        @Test
        fun list() {
            val src = mapper.writeValueAsString(AnyWrapper(arrayOf<Int?>(null)))
            assertThrows<InvalidNullException> { mapper.readValue<ListWrapper>(src) }
        }

        @Test
        fun map() {
            val src = mapper.writeValueAsString(AnyWrapper(mapOf("foo" to null)))
            assertThrows<InvalidNullException> { mapper.readValue<MapWrapper>(src) }
        }
    }

    class ContentNullsSkipArrayWrapper(@JsonSetter(contentNulls = Nulls.SKIP) val value: Array<Int>)
    data class ContentNullsSkipListWrapper(@JsonSetter(contentNulls = Nulls.SKIP) val value: List<Int>)
    data class ContentNullsSkipMapWrapper(@JsonSetter(contentNulls = Nulls.SKIP) val value: Map<String, Int>)

    @Nested
    inner class CustomByAnnotationTest {
        @Test
        fun array() {
            val expected = ContentNullsSkipArrayWrapper(emptyArray())
            val src = mapper.writeValueAsString(AnyWrapper(arrayOf<Int?>(null)))
            val result = mapper.readValue<ContentNullsSkipArrayWrapper>(src)

            assertArrayEquals(expected.value, result.value)
        }

        @Test
        fun list() {
            val expected = ContentNullsSkipListWrapper(emptyList())
            val src = mapper.writeValueAsString(AnyWrapper(listOf<Int?>(null)))
            val result = mapper.readValue<ContentNullsSkipListWrapper>(src)

            assertEquals(expected, result)
        }

        @Test
        fun map() {
            val expected = ContentNullsSkipMapWrapper(emptyMap())
            val src = mapper.writeValueAsString(AnyWrapper(mapOf("foo" to null)))
            val result = mapper.readValue<ContentNullsSkipMapWrapper>(src)

            assertEquals(expected, result)
        }
    }

    class AnnotatedArrayWrapper(@JsonSetter(nulls = Nulls.SKIP) val value: Array<Int> = emptyArray())
    data class AnnotatedListWrapper(@JsonSetter(nulls = Nulls.SKIP) val value: List<Int> = emptyList())
    data class AnnotatedMapWrapper(@JsonSetter(nulls = Nulls.SKIP) val value: Map<String, Int> = emptyMap())

    // If Default is specified by annotation, it is not overridden.
    @Nested
    inner class AnnotatedNullInput {
        @Test
        fun array() {
            val src = mapper.writeValueAsString(AnyWrapper(arrayOf<Int?>(null)))
            assertThrows<InvalidNullException> { mapper.readValue<AnnotatedArrayWrapper>(src) }
        }

        @Test
        fun list() {
            val src = mapper.writeValueAsString(AnyWrapper(arrayOf<Int?>(null)))
            assertThrows<InvalidNullException> { mapper.readValue<AnnotatedListWrapper>(src) }
        }

        @Test
        fun map() {
            val src = mapper.writeValueAsString(AnyWrapper(mapOf("foo" to null)))
            assertThrows<InvalidNullException> { mapper.readValue<AnnotatedMapWrapper>(src) }
        }
    }
}
