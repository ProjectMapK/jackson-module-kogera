package io.github.projectmapk.jackson.module.kogera.deser

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.databind.util.Converter
import com.fasterxml.jackson.databind.util.StdConverter

internal class ValueClassUnboxConverter<T : Any>(private val valueClass: Class<T>) : StdConverter<T, Any?>() {
    private val unboxMethod = valueClass.getDeclaredMethod("unbox-impl").apply {
        if (!this.isAccessible) this.isAccessible = true
    }

    override fun convert(value: T): Any? = unboxMethod.invoke(value)

    override fun getInputType(typeFactory: TypeFactory): JavaType = typeFactory.constructType(valueClass)
}

internal sealed class CollectionValueStrictNullChecksConverter<T : Any> : Converter<T, T> {
    protected abstract val type: JavaType
    protected abstract val paramName: String

    protected abstract fun getValues(value: T): Iterator<*>

    override fun convert(value: T): T {
        getValues(value).forEach {
            if (it == null) {
                throw MismatchedInputException.from(
                    null,
                    null as JavaType?,
                    "A null value was entered for the parameter $paramName."
                )
            }
        }

        return value
    }

    override fun getInputType(typeFactory: TypeFactory): JavaType = type
    override fun getOutputType(typeFactory: TypeFactory): JavaType = type

    class ForIterable(
        override val type: JavaType,
        override val paramName: String
    ) : CollectionValueStrictNullChecksConverter<Iterable<*>>() {
        override fun getValues(value: Iterable<*>): Iterator<*> = value.iterator()
    }

    class ForArray constructor(
        override val type: JavaType,
        override val paramName: String
    ) : CollectionValueStrictNullChecksConverter<Array<*>>() {
        override fun getValues(value: Array<*>): Iterator<*> = value.iterator()
    }
}

internal class MapValueStrictNullChecksConverter(
    private val type: JavaType,
    private val paramName: String
) : Converter<Map<*, *>, Map<*, *>> {
    override fun convert(value: Map<*, *>): Map<*, *> = value.apply {
        entries.forEach { (k, v) ->
            if (v == null) {
                throw MismatchedInputException.from(
                    null,
                    null as JavaType?,
                    "A null value was entered for key $k of the parameter $paramName."
                )
            }
        }
    }

    override fun getInputType(typeFactory: TypeFactory): JavaType = type
    override fun getOutputType(typeFactory: TypeFactory): JavaType = type
}
