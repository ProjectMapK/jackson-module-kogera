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
     * Enabling it protects against this but has performance impact.
     */
    StrictNullChecks(enabledByDefault = false);

    internal val bitSet: BitSet = (1 shl ordinal).toBitSet()

    public companion object {
        internal val defaults
            get() = values().fold(BitSet(Int.SIZE_BITS)) { acc, cur ->
                acc.apply { if (cur.enabledByDefault) this.or(cur.bitSet) }
            }
    }
}
