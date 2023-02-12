The `jackson-module-kogera` supports many use cases of `value class` (`inline class`).  
This page summarizes the basic policy and points to note regarding the use of the `value class`.

## Note on the use of `value class`
`value class` is one of the distinctive features of `Kotlin`.  
Many key use cases are not supported in `jackson-module-kotlin` because
the functions and properties associated with `value class` have a special representation on the `JVM`.

However, due to `Jackson` limitations, the same behavior as the normal class is not fully reproduced.  
Known issues related to `value class` can be found [here](https://github.com/ProjectMapK/jackson-module-kogera/issues?q=is%3Aissue+is%3Aopen+label%3A%22value+class%22).

Also, when using `Jackson`, there is a concern that the use of `value class` will rather degrade performance.  
This is because `jackson-module-kogera` does a lot of reflection processing to support `value class`
(this concern will be confirmed in [kogera-benchmark](https://github.com/ProjectMapK/kogera-benchmark) in the future).

For these reasons, I recommend careful consideration when using `value class`.

## Basic handling of `value class`
A `value class` is basically treated like a value.

For example, the serialization of `value class` is as follows

```kotlin
@JvmInline
value class Value(val value: Int)

val mapper = jacksonObjectMapper()
mapper.writeValueAsString(Value(1)) // -> 1
```

This is different from the `data class` serialization result.

```kotlin
data class Data(val value: Int)

mapper.writeValueAsString(Data(1)) // -> {"value":1}
```

The same policy applies to deserialization.

This policy was decided with reference to the behavior as of `jackson-module-kotlin 2.14.1` and [kotlinx-serialization](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/value-classes.md#serializable-value-classes).  
However, these are just basic policies, and the behavior can be overridden with `JsonSerializer` or `JsonDeserializer`.

## For features that would not be supported
I do not intend to support features that require significant effort to support and that can be circumvented by user definition.

### Serialization considering custom properties
The `value class` can define custom getters such as the following, which will be ignored during serialization.

```kotlin
@JvmInline
value class Value(val value: Int) {
    val stringValue: String get() = value.toString()
}
```

### Deserialization from multiple properties using `JsonCreator`
In a normal class, deserialization from multiple properties can be performed by setting up a factory function as follows.

```kotlin
data class Data(val value: Int) {
    companion object {
        @JvmStatic
        @JsonCreator
        fun creator(foo: Int, bar: Int) = Data(foo + bar)
    }
}

val mapper = jacksonObjectMapper()
val data = mapper.readValue<Data>("""{"foo":1,"bar":2}""") // -> Data(value=3)
```

In the `value class`, such factory functions are basically not available (they raise an `InvalidDefinitionException`).
