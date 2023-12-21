The `jackson-module-kogera` supports many use cases of `value class` (`inline class`).  
This page summarizes the basic policy and points to note regarding the use of the `value class`.

## Note on the use of `value class`
The `value class` is one of the `Kotlin` specific feature.  
On the other hand, `jackson-module-kotlin` does not support deserialization of `value class` in particular.  
Also, there are some features of serialization that do not work properly.

The reason for this is that `value class` is a special representation on the `JVM`.  
Due to this difference, some cases cannot be handled by basic `Jackson` parsing, which assumes `Java`.  
Known issues related to `value class` can be found [here](https://github.com/ProjectMapK/jackson-module-kogera/issues?q=is%3Aissue+is%3Aopen+label%3A%22value+class%22).

In addition, one of the features of the `value class` is improved performance,
but when using `Jackson` (not only `Jackson`, but also other libraries that use reflection),
the performance is rather reduced.  
This can be confirmed from [kogera-benchmark](https://github.com/ProjectMapK/kogera-benchmark).

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

### Serialization performance improvement using `JsonUnbox`
In `jackson-module-kogera`, the `jackson` functionality is modified by reflection so that the `Jackson` functionality works for `value class` as well.
These are executed on all calls.
On the other hand, if only `unbox` is required, these are extra overheads that can significantly impair serialization performance.

Therefore, `jackson-module-kogera` provides the `JsonUnbox` annotation.
When this annotation is provided, serialization performance is improved because only calls to `getter` that is `unboxed` will be made.

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
