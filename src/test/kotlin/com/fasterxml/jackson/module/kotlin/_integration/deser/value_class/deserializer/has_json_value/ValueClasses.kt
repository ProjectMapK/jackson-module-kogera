package com.fasterxml.jackson.module.kotlin._integration.deser.value_class.deserializer.has_json_value

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

@JvmInline
value class Primitive(@get:JsonValue val v: Int) {
    class Deserializer : StdDeserializer<Primitive>(Primitive::class.java) {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Primitive = Primitive(p.intValue)
    }
}

@JvmInline
value class NonNullObject(@get:JsonValue val v: String) {
    class Deserializer : StdDeserializer<NonNullObject>(NonNullObject::class.java) {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): NonNullObject =
            NonNullObject(p.valueAsString)
    }
}

@JvmInline
value class NullableObject(@get:JsonValue val v: String?) {
    class Deserializer : StdDeserializer<NullableObject>(NullableObject::class.java) {
        // After https://github.com/ProjectMapK/jackson-module-kogera/issues/42 is resolved, uncomment out
        // override fun getNullValue(ctxt: DeserializationContext): NullableObject = NullableObject(null)

        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): NullableObject =
            NullableObject(p.valueAsString)
    }
}
