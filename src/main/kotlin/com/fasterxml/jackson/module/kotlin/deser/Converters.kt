package com.fasterxml.jackson.module.kotlin.deser

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.databind.util.StdConverter
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.creator.ValueParameter

internal class ValueClassUnboxConverter<T : Any>(private val valueClass: Class<T>) : StdConverter<T, Any?>() {
    private val unboxMethod = valueClass.getDeclaredMethod("unbox-impl").apply {
        if (!this.isAccessible) this.isAccessible = true
    }

    override fun convert(value: T): Any? = unboxMethod.invoke(value)

    override fun getInputType(typeFactory: TypeFactory): JavaType = typeFactory.constructType(valueClass)
}

internal sealed class CollectionValueStrictNullChecksConverter<T : Any> : StdConverter<T, T>() {
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

    class ForIterable(
        override val valueParameter: ValueParameter
    ) : CollectionValueStrictNullChecksConverter<Iterable<*>>() {
        override fun getValues(value: Iterable<*>): Iterator<*> = value.iterator()
    }

    class ForArray(
        override val valueParameter: ValueParameter
    ) : CollectionValueStrictNullChecksConverter<Array<*>>() {
        override fun getValues(value: Array<*>): Iterator<*> = value.iterator()
    }
}

internal class MapValueStrictNullChecksConverter(
    private val valueParameter: ValueParameter
) : StdConverter<Map<*, *>, Map<*, *>>() {
    override fun convert(value: Map<*, *>): Map<*, *> = value.apply {
        entries.forEach { (k, v) ->
            if (v == null) {
                throw MissingKotlinParameterException(
                    valueParameter,
                    null,
                    "A null value was entered for key $k of the parameter ${valueParameter.name}."
                )
            }
        }
    }
}
