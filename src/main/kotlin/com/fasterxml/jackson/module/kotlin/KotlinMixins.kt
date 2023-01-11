package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

internal abstract class ClosedRangeMixin<T> @JsonCreator constructor(
    public val start: T,
    @get:JsonProperty("end") public val endInclusive: T
) {
    @JsonIgnore public abstract fun getEnd(): T

    @JsonIgnore public abstract fun getFirst(): T

    @JsonIgnore public abstract fun getLast(): T

    @JsonIgnore public abstract fun getIncrement(): T

    @JsonIgnore public abstract fun isEmpty(): Boolean

    @JsonIgnore public abstract fun getStep(): T

    @JsonIgnore public abstract fun getEndExclusive(): T
}
