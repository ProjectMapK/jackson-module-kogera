package com.fasterxml.jackson.module.kotlin._integration.ser.value_class.json_include

@JvmInline
value class Primitive(val v: Int)

@JvmInline
value class NonNullObject(val v: String)

@JvmInline
value class NullableObject(val v: String?)
