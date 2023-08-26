package io.github.projectmapk.jackson.module.kogera.ser

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.databind.util.StdConverter
import io.github.projectmapk.jackson.module.kogera.JavaDuration
import io.github.projectmapk.jackson.module.kogera.KotlinDuration
import kotlin.time.toJavaDuration

internal class SequenceToIteratorConverter(private val input: JavaType) : StdConverter<Sequence<*>, Iterator<*>>() {
    override fun convert(value: Sequence<*>): Iterator<*> = value.iterator()

    override fun getInputType(typeFactory: TypeFactory): JavaType = input

    // element-type may not be obtained, so a null check is required
    override fun getOutputType(typeFactory: TypeFactory): JavaType = input.containedType(0)
        ?.let { typeFactory.constructCollectionLikeType(Iterator::class.java, it) }
        ?: typeFactory.constructType(Iterator::class.java)
}

// S is nullable because value corresponds to a nullable value class
// @see KotlinFallbackAnnotationIntrospector.findNullSerializer
internal class ValueClassBoxConverter<S : Any?, D : Any>(
    unboxedClass: Class<S>,
    valueClass: Class<D>
) : StdConverter<S, D>() {
    private val boxMethod = valueClass.getDeclaredMethod("box-impl", unboxedClass).apply {
        if (!this.isAccessible) this.isAccessible = true
    }

    @Suppress("UNCHECKED_CAST")
    override fun convert(value: S): D = boxMethod.invoke(null, value) as D

    val delegatingSerializer: StdDelegatingSerializer by lazy { StdDelegatingSerializer(this) }
}

internal object KotlinDurationValueToJavaDurationConverter : StdConverter<Long, JavaDuration>() {
    private val boxConverter by lazy { ValueClassBoxConverter(Long::class.java, KotlinDuration::class.java) }

    override fun convert(value: Long): JavaDuration = KotlinToJavaDurationConverter.convert(boxConverter.convert(value))
}

internal object KotlinToJavaDurationConverter : StdConverter<KotlinDuration, JavaDuration>() {
    override fun convert(value: KotlinDuration) = value.toJavaDuration()
}
