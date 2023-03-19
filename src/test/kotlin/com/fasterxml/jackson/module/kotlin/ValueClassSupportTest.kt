package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.module.kotlin.support.ValueClassSupport.boxedValue
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ValueClassSupportTest {

    @JvmInline
    value class TestValueClass(val value: String)

    @Test
    fun throwExceptionIfUseBoxedValueMethodWithNonValueClass() {
        val test = "test"


        val exception = assertThrows<UnsupportedOperationException> {
            test.boxedValue
        }

        Assertions.assertEquals("$test is not a value class", exception.message)
    }

    @Test
    fun getBoxedValue() {
        val test = TestValueClass("test")

        Assertions.assertEquals("test", test.boxedValue)
    }
}
