package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.module.SimpleModule
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ExtensionsTest {
    @Nested
    inner class AddDeserializerTest {
        val module = mockk<SimpleModule> {
            every { addDeserializer(any<Class<*>>(), any()) } returns this
        }

        @Test
        fun primitiveType() {
            val mockDeserializer: JsonDeserializer<Double> = mockk()
            module.addDeserializer(Double::class, mockDeserializer)

            verify(exactly = 1) { module.addDeserializer(Double::class.javaPrimitiveType, mockDeserializer) }
            verify(exactly = 1) { module.addDeserializer(Double::class.javaObjectType, mockDeserializer) }
        }

        @Test
        fun objectType() {
            val mockDeserializer: JsonDeserializer<Any> = mockk()
            module.addDeserializer(Any::class, mockDeserializer)

            verify(exactly = 1) { module.addDeserializer(Any::class.javaObjectType, mockDeserializer) }
            confirmVerified(module)
        }

        @Test
        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
        fun wrapperType() {
            val mockDeserializer: JsonDeserializer<Integer> = mockk()
            module.addDeserializer(Integer::class, mockDeserializer)

            verify(exactly = 1) { module.addDeserializer(Integer::class.javaPrimitiveType, mockDeserializer) }
            verify(exactly = 1) { module.addDeserializer(Integer::class.javaObjectType, mockDeserializer) }
        }
    }

    @Nested
    inner class AddSerializerTest {
        val module = mockk<SimpleModule> {
            every { addSerializer(any<Class<*>>(), any()) } returns this
        }

        @Test
        fun primitiveType() {
            val mockSerializer: JsonSerializer<Double> = mockk()
            module.addSerializer(Double::class, mockSerializer)

            verify(exactly = 1) { module.addSerializer(Double::class.javaPrimitiveType, mockSerializer) }
            verify(exactly = 1) { module.addSerializer(Double::class.javaObjectType, mockSerializer) }
        }

        @Test
        fun objectType() {
            val mockSerializer: JsonSerializer<Any> = mockk()
            module.addSerializer(Any::class, mockSerializer)

            verify(exactly = 1) { module.addSerializer(Any::class.javaObjectType, mockSerializer) }
            confirmVerified(module)
        }

        @Test
        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
        fun wrapperType() {
            val mockSerializer: JsonSerializer<Integer> = mockk()
            module.addSerializer(Integer::class, mockSerializer)

            verify(exactly = 1) { module.addSerializer(Integer::class.javaPrimitiveType, mockSerializer) }
            verify(exactly = 1) { module.addSerializer(Integer::class.javaObjectType, mockSerializer) }
        }
    }
}
