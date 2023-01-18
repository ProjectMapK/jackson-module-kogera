package com.fasterxml.jackson.module.kotlin.deser

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.databind.util.Converter
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.creator.ValueParameter

internal class ValueClassUnboxConverter<T : Any>(private val valueClass: Class<T>) : Converter<T, Any?> {
    private val unboxMethod = valueClass.getDeclaredMethod("unbox-impl").apply {
        if (!this.isAccessible) this.isAccessible = true
    }
    private val outType = unboxMethod.returnType

    override fun convert(value: T): Any? = unboxMethod.invoke(value)

    override fun getInputType(typeFactory: TypeFactory): JavaType = typeFactory.constructType(valueClass)
    override fun getOutputType(typeFactory: TypeFactory): JavaType = typeFactory.constructType(outType)
}

internal sealed class StrictNullChecksConverter<T : Any> : Converter<T, T> {
    protected abstract val clazz: Class<T>
    protected abstract val valueParameter: ValueParameter

    protected abstract fun getValues(value: T): Iterator<*>

    override fun convert(value: T): T {
        getValues(value).forEach {
            if (it == null) {
                throw MissingKotlinParameterException(
                    valueParameter,
                    null,
                    "A null value was entered for the parameter ${valueParameter.name}."
                )
            }
        }

        return value
    }

    override fun getInputType(typeFactory: TypeFactory): JavaType = typeFactory.constructType(clazz)
    override fun getOutputType(typeFactory: TypeFactory): JavaType = typeFactory.constructType(clazz)

    class ForIterable(
        override val clazz: Class<Iterable<*>>,
        override val valueParameter: ValueParameter
    ) : StrictNullChecksConverter<Iterable<*>>() {
        override fun getValues(value: Iterable<*>): Iterator<*> = value.iterator()
    }

    class ForArray(
        override val clazz: Class<Array<*>>,
        override val valueParameter: ValueParameter
    ) : StrictNullChecksConverter<Array<*>>() {
        override fun getValues(value: Array<*>): Iterator<*> = value.iterator()
    }

    class ForMapValue(
        override val clazz: Class<Map<*, *>>,
        override val valueParameter: ValueParameter
    ) : StrictNullChecksConverter<Map<*, *>>() {
        override fun getValues(value: Map<*, *>): Iterator<*> = value.values.iterator()
    }
}
