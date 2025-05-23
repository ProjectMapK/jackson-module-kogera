package io.github.projectmapk.jackson.module.kogera.zIntegration.ser.valueClass.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer

@JvmInline
value class Primitive(val v: Int) {
    class Serializer : StdSerializer<Primitive>(Primitive::class.java) {
        override fun serialize(value: Primitive, gen: JsonGenerator, provider: SerializerProvider) {
            gen.writeNumber(value.v + 100)
        }
    }
}

@JvmInline
value class NonNullObject(val v: String) {
    class Serializer : StdSerializer<NonNullObject>(NonNullObject::class.java) {
        override fun serialize(value: NonNullObject, gen: JsonGenerator, provider: SerializerProvider) {
            gen.writeString("${value.v}-ser")
        }
    }
}

@JvmInline
value class NullableObject(val v: String?) {
    class Serializer : StdSerializer<NullableObject>(NullableObject::class.java) {
        override fun serialize(value: NullableObject, gen: JsonGenerator, provider: SerializerProvider) {
            gen.writeString(value.v?.let { "$it-ser" } ?: "NULL")
        }
    }
}

@JvmInline
value class NullablePrimitive(val v: Int?) {
    class Serializer : StdSerializer<NullablePrimitive>(NullablePrimitive::class.java) {
        override fun serialize(value: NullablePrimitive, gen: JsonGenerator, provider: SerializerProvider) {
            value.v?.let { gen.writeNumber(it + 100) } ?: gen.writeString("NULL")
        }
    }
}

@JvmInline
value class TwoUnitPrimitive(val v: Long) {
    class Serializer : StdSerializer<TwoUnitPrimitive>(TwoUnitPrimitive::class.java) {
        override fun serialize(value: TwoUnitPrimitive, gen: JsonGenerator, provider: SerializerProvider) {
            gen.writeNumber(value.v + 100)
        }
    }
}
