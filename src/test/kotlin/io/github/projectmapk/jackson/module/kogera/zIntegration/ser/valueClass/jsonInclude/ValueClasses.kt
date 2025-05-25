package io.github.projectmapk.jackson.module.kogera.zIntegration.ser.valueClass.jsonInclude

@JvmInline
value class Primitive(val v: Int)

@JvmInline
value class NonNullObject(val v: String)

@JvmInline
value class NullableObject(val v: String?)

@JvmInline
value class NullablePrimitive(val v: Int?)

@JvmInline
value class TwoUnitPrimitive(val v: Long)
