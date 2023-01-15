package com.fasterxml.jackson.module.kotlin.deser

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.databind.util.Converter

internal class ValueClassUnboxConverter<T>(private val valueClass: Class<T>) : Converter<T, Any?> {
    private val unboxMethod = valueClass.getDeclaredMethod("unbox-impl").apply {
        if (!this.isAccessible) this.isAccessible = true
    }
    private val outType = unboxMethod.returnType

    override fun convert(value: T): Any? = unboxMethod.invoke(value)

    override fun getInputType(typeFactory: TypeFactory): JavaType = typeFactory.constructType(valueClass)
    override fun getOutputType(typeFactory: TypeFactory): JavaType = typeFactory.constructType(outType)
}
