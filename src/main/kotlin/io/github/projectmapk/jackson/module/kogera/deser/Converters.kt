package io.github.projectmapk.jackson.module.kogera.deser

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.databind.util.Converter
import com.fasterxml.jackson.databind.util.StdConverter
import io.github.projectmapk.jackson.module.kogera.JavaDuration
import io.github.projectmapk.jackson.module.kogera.KotlinDuration
import kotlin.time.toKotlinDuration

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

/**
 * Currently it is not possible to deduce type of [kotlin.time.Duration] fields therefore explicit annotation is needed on fields in order to properly deserialize POJO.
 *
 * @see [com.fasterxml.jackson.module.kotlin.test.DurationTests]
 */
internal object JavaToKotlinDurationConverter : StdConverter<JavaDuration, KotlinDuration>() {
    override fun convert(value: JavaDuration) = value.toKotlinDuration()

    val delegatingDeserializer: StdDelegatingDeserializer<KotlinDuration> by lazy {
        StdDelegatingDeserializer(this)
    }
}
