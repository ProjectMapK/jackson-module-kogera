package io.github.projectmapk.jackson.module.kogera.deser

import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer
import com.fasterxml.jackson.databind.util.StdConverter
import io.github.projectmapk.jackson.module.kogera.JavaDuration
import io.github.projectmapk.jackson.module.kogera.KotlinDuration
import kotlin.time.toKotlinDuration

/**
 * Currently it is not possible to deduce type of [kotlin.time.Duration] fields therefore explicit annotation is needed on fields in order to properly deserialize POJO.
 *
 * @see [io.github.projectmapk.jackson.module.kogera.zIntegration.DurationTest]
 */
internal object JavaToKotlinDurationConverter : StdConverter<JavaDuration, KotlinDuration>() {
    override fun convert(value: JavaDuration) = value.toKotlinDuration()

    val delegatingDeserializer: StdDelegatingDeserializer<KotlinDuration> by lazy {
        StdDelegatingDeserializer(this)
    }
}
