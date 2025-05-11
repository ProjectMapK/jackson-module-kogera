package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.annotation.JsonInclude
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class GitHub625 {
    @JvmInline
    value class Primitive(val v: Int)

    @JvmInline
    value class NonNullObject(val v: String)

    @JvmInline
    value class NullableObject(val v: String?)

    @JsonInclude(value = JsonInclude.Include.NON_NULL, content = JsonInclude.Include.NON_NULL)
    data class Dto(
        val primitive: Primitive? = null,
        val nonNullObject: NonNullObject? = null,
        val nullableObject: NullableObject? = null
    ) {
        fun getPrimitiveGetter(): Primitive? = null
        fun getNonNullObjectGetter(): NonNullObject? = null
        fun getNullableObjectGetter(): NullableObject? = null
    }

    @Test
    fun test() {
        val dto = Dto()
        assertEquals("{}", defaultMapper.writeValueAsString(dto))
    }

    @JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
    data class FailingDto(
        val nullableObject1: NullableObject = NullableObject(null),
        val nullableObject2: NullableObject? = NullableObject(null),
        val map: Map<Any, Any?> = mapOf("nullableObject" to NullableObject(null),)
    ) {
        fun getNullableObjectGetter1(): NullableObject = NullableObject(null)
        fun getNullableObjectGetter2(): NullableObject? = NullableObject(null)
        fun getMapGetter(): Map<Any, Any?> = mapOf("nullableObject" to NullableObject(null))
    }

    @Test
    fun failing() {
        val json = defaultMapper.writeValueAsString(FailingDto())

        assertNotEquals("{}", json)
    }
}
