A list of issues that have not been resolved in `jackson-module-kotlin`, but have been or will be resolved in `kogera`.

## Fixed
- [Update `KotlinModule` to override `AnnotationIntrospector.findCreatorAnnotation()` instead of `hasCreatorAnnotation()` · Issue \#200](https://github.com/FasterXML/jackson-module-kotlin/issues/200)
- [Getting MismatchedInputException instead of MissingKotlinParameterException · Issue \#234](https://github.com/FasterXML/jackson-module-kotlin/issues/234)
- [Private getter with different return type hides property · Issue \#341](https://github.com/FasterXML/jackson-module-kotlin/issues/341)
- [Remove \`kotlin\-reflect\` and replace it with \`kotlinx\-metadata\-jvm\` · Issue \#450](https://github.com/FasterXML/jackson-module-kotlin/issues/450)
- [Remove deprecated method in KNAI · Issue \#508](https://github.com/FasterXML/jackson-module-kotlin/issues/508)
- [Support JsonKey in value class · Issue \#536](https://github.com/FasterXML/jackson-module-kotlin/issues/536)
- [Type Introspection: non\-nullable field with default is reported as required · Issue \#609](https://github.com/FasterXML/jackson-module-kotlin/issues/609)
- [`JsonSerializer` is enabled when the value is an Object type with non\-null value and the property definition is nullable. · Issue \#618](https://github.com/FasterXML/jackson-module-kotlin/issues/618)
- [About the problem that property names in \`Jackson\` and definitions in \`Kotlin\` are sometimes different\. · Issue \#630](https://github.com/FasterXML/jackson-module-kotlin/issues/630)
- [Annotation given to constructor parameters containing \`value class\` as argument does not work · Issue \#651](https://github.com/FasterXML/jackson-module-kotlin/issues/651)
- [How to deserialize a kotlin\.ranges\.ClosedRange<T> with Jackson · Issue \#663](https://github.com/FasterXML/jackson-module-kotlin/issues/663)
- [There are cases where \`isRequired\` specifications are not properly merged\. · Issue \#668](https://github.com/FasterXML/jackson-module-kotlin/issues/668)

## Maybe fixed(verification required)
- [@JsonProperty is ignored on data class properties with names starting with "is" · Issue \#237](https://github.com/FasterXML/jackson-module-kotlin/issues/237)

## Want to fix
- [Support for inline classes · Issue \#199](https://github.com/FasterXML/jackson-module-kotlin/issues/199)
- [There are some problems with KNAI\.hasCreatorAnnotation · Issue \#547](https://github.com/FasterXML/jackson-module-kotlin/issues/547)
- [This module shouldn't bring kotlin\-reflect 1\.5 as a transitive dependency · Issue \#566](https://github.com/FasterXML/jackson-module-kotlin/issues/566)
- [ReflectionCache takes a lot of memory · Issue \#584](https://github.com/FasterXML/jackson-module-kotlin/issues/584)
- [Supports deserialization of \`value class\` \(\`inline class\`\)\. · Issue \#650](https://github.com/FasterXML/jackson-module-kotlin/issues/650)
