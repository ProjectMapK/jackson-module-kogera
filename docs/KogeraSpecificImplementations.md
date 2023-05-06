In `jackson-module-kogera`, there are several intentional design changes that are destructive.  
This page summarizes them.

# Common
## Set `Kotlin Property` as positive and ignore functions.
In `jackson-module-kotlin`, functions with getterLike or setterLike names are handled in the same way as `Kotlin Property`.  
On the other hand, this implementation causes discrepancies between the `Kotlin` description and the processing results by `Jackson`.

Therefore, `kogera` processes only `Kotlin Property` and excludes other functions from processing.  
In addition, `kogera` uses the content defined in `Kotlin` as its name.

These changes make it impossible to manipulate the results using `JvmName`.

## Change of `hasRequiredMarker` specification
The `KotlinAnnotationIntrospector::hasRequiredMarker` function in `jackson-module-kotlin` has several problems.
In `kogera`, the specification has been revised as follows.

- If `required = true` is specified in the `JsonProperty` annotation, the result is preferred.
    - This should not be overridden, as it will not be `true` unless explicitly specified by the user.
    - This implementation follows the [basic policy of `Jackson`](https://github.com/ProjectMapK/jackson-module-kogera/pull/101#issuecomment-1527739305).
- Processing on `field` that has accessors are skipped.
- The `FAIL_ON_NULL_FOR_PRIMITIVES` option does not affect the results.
    - In `kogera`, the deserialization behavior is not affected by whether the argument is of type `primitive` or not.
- The `nullToEmpty` option only affects deserialization.
    - As for `JvmField`, there was no way to distinguish between contexts, so that option affects serialization.

## Stricter visibility and privatization of some codes
In `jackson-module-kotlin`, some code has been inadvertently released.
If code is released inadvertently, it will be difficult to change it considering dependencies.

Therefore, in `kogera`, `explicitApi` is enabled and all but the minimum code is kept private.
I will consider making the classes public again when we receive requests for them.

## Remove old options and `deprecated` code
Because `jackson-module-kotlin` is a framework with a long history, some old code and options remain.  
In `kogera`, those options have been removed.
