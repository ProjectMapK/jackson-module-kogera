package io.github.projectmapk.jackson.module.kogera.ser

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.databind.util.StdConverter
import io.github.projectmapk.jackson.module.kogera.JavaDuration
import io.github.projectmapk.jackson.module.kogera.KOTLIN_DURATION_CLASS
import io.github.projectmapk.jackson.module.kogera.KotlinDuration
import io.github.projectmapk.jackson.module.kogera.LongValueClassBoxConverter
import kotlin.time.toJavaDuration

internal class SequenceToIteratorConverter(private val input: JavaType) : StdConverter<Sequence<*>, Iterator<*>>() {
    override fun convert(value: Sequence<*>): Iterator<*> = value.iterator()

    override fun getInputType(typeFactory: TypeFactory): JavaType = input

    // element-type may not be obtained, so a null check is required
    override fun getOutputType(typeFactory: TypeFactory): JavaType = input.containedType(0)
        ?.let { typeFactory.constructCollectionLikeType(Iterator::class.java, it) }
        ?: typeFactory.constructType(Iterator::class.java)
}

internal object KotlinDurationValueToJavaDurationConverter : StdConverter<Long, JavaDuration>() {
    private val boxConverter by lazy { LongValueClassBoxConverter(KOTLIN_DURATION_CLASS) }

    override fun convert(value: Long): JavaDuration = KotlinToJavaDurationConverter.convert(boxConverter.convert(value))
}

internal object KotlinToJavaDurationConverter : StdConverter<KotlinDuration, JavaDuration>() {
    override fun convert(value: KotlinDuration) = value.toJavaDuration()
}
