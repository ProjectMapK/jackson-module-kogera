package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GitHub618 {
    @JsonSerialize(using = V.Serializer::class)
    @JvmInline
    value class V(val value: String) {
        class Serializer : StdSerializer<V>(V::class.java) {
            override fun serialize(p0: V, p1: JsonGenerator, p2: SerializerProvider) {
                p1.writeString(p0.toString())
            }
        }
    }

    data class D(val v: V?)

    @Test
    fun test() {
        // expected: {"v":null}, but NullPointerException thrown
        assertEquals("""{"v":null}""", defaultMapper.writeValueAsString(D(null)))
    }
}
