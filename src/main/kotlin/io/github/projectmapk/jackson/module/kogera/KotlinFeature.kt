package io.github.projectmapk.jackson.module.kogera

import java.util.BitSet

private fun Int.toBitSet(): BitSet {
    var i = this
    var index = 0
    val bits = BitSet(Int.SIZE_BITS)
    while (i != 0) {
        if (i and 1 != 0) {
            bits.set(index)
        }
        ++index
        i = i shr 1
    }
    return bits
}

/**
 * @see KotlinModule.Builder
 */
public enum class KotlinFeature(internal val enabledByDefault: Boolean) {
    /**
     * This feature represents whether to deserialize `null` values for collection properties as empty collections.
     */
    NullToEmptyCollection(enabledByDefault = false),

    /**
     * This feature represents whether to deserialize `null` values for a map property to an empty map object.
     */
    NullToEmptyMap(enabledByDefault = false),

    /**
     * This feature represents whether to treat `null` values as absent when deserializing,
     * thereby using the default value provided in Kotlin.
     */
    NullIsSameAsDefault(enabledByDefault = false),

    /**
     * By default, there's no special handling of singletons (pre-2.10 behavior).
     * Each time a Singleton object is deserialized a new instance is created.
     *
     * When this feature is enabled, it will deserialize then canonicalize (was the default in 2.10).
     * Deserializing a singleton overwrites the value of the single instance.
     *
     * See [jackson-module-kotlin#225]: keep Kotlin singletons as singletons.
     * @see io.github.projectmapk.jackson.module.kogera.SingletonSupport
     */
    SingletonSupport(enabledByDefault = false),

    /**
     * This feature represents whether to check deserialized collections.
     *
     * With this disabled, the default, collections which are typed to disallow null members (e.g. `List<String>`)
     * may contain null values after deserialization.
     * Enabling it protects against this, but it impairs performance a bit.
     *
     * Also, if contentNulls are custom from findSetterInfo in AnnotationIntrospector, there may be a conflict.
     */
    StrictNullChecks(enabledByDefault = false),

    /**
     * This feature represents whether to include in Jackson's parsing the annotations given to the parameters of
     * constructors that include value class as a parameter.
     *
     * Constructor with value class as a parameter on Kotlin are compiled into a public synthetic constructor
     * and a private constructor.
     * In this case, annotations are only given to synthetic constructors,
     * so they are not normally included in Jackson's parsing and will not work.
     *
     * To work around this problem, annotations can be added by specifying a field or getter,
     * or by enabling this feature.
     * However, enabling this feature will affect initialization performance.
     * Also note that enabling this feature does not enable annotations given to the constructor.
     *
     * @see KotlinClassIntrospector
     */
    CopySyntheticConstructorParameterAnnotations(enabledByDefault = false),

    /**
     * This feature represents whether to handle [kotlin.time.Duration] using [java.time.Duration] as conversion bridge.
     *
     * This allows use Kotlin Duration type with [com.fasterxml.jackson.datatype.jsr310.JavaTimeModule].
     * `@JsonFormat` annotations need to be declared either on getter using `@get:JsonFormat` or field using `@field:JsonFormat`.
     * See [jackson-module-kotlin#651] for details.
     */
    UseJavaDurationConversion(enabledByDefault = false);

    internal val bitSet: BitSet = (1 shl ordinal).toBitSet()

    public companion object {
        internal val defaults
            get() = values().fold(BitSet(Int.SIZE_BITS)) { acc, cur ->
                acc.apply { if (cur.enabledByDefault) this.or(cur.bitSet) }
            }
    }
}
