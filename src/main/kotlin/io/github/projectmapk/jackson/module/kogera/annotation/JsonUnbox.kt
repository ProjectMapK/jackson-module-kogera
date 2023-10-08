package io.github.projectmapk.jackson.module.kogera.annotation

/**
 * Only affects properties that return value class and value class.
 * If given, parsing to apply `Jackson` features will be skipped and only unbox will be performed.
 * This will prevent various features such as JsonValue and custom serializers from working,
 * but will improve serialization performance.
 */
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY_GETTER)
public annotation class JsonUnbox
