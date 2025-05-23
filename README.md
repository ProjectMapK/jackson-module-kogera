jackson-module-kogera
====

Since `beta-23`, newer versions of `Kotlin` and `Java` than the original have been used to validate changes for `Jackson 3.x`.  
See #Compatibility for details.

---

`jackson-module-kogera` is an experimental project to develop `jackson-module-kotlin`.  
This project has the following features compared to `jackson-module-kotlin`.

- Lightweight
- high-performance
  - Fast deserialization
  - Smaller memory consumption
- More `Kotlin` friendly behavior

Especially when it comes to deserialization throughput, it is several times higher than `jackson-module-kotlin`.
![](https://docs.google.com/spreadsheets/d/e/2PACX-1vSDpaOENd0a-qO_zK7C5_UkSxEKk7BxLjmyg8XVnPP0jj6J5rgoA8cCnm_lj7lflx6NDjvC1yMUPrce/pubchart?oid=1594997844&format=image)

This project is experimental, but passes all the tests implemented in `jackson-module-kotlin` except for the intentional incompatibility.

# Features of `jackson-module-kogera`
The main feature of `jackson-module-kogera` is that it replaces `kotlin-reflect` with `kotlin.metadata.jvm`.  
As of `1.7.21`, `kotlin-reflect` is a huge library(3MB), and replacing it with `kotlin.metadata.jvm`(1MB) makes it lightweight.

Several performance improvements have also been made.
First, by implementing the equivalent of https://github.com/FasterXML/jackson-module-kotlin/pull/439, deserialization is now up to three times faster, depending on the use case.  
The cache has also been reorganized based on [benchmark results](https://github.com/ProjectMapK/kogera-benchmark) to achieve smaller memory consumption.  
The performance degradation when the `strictNullChecks` option is enabled is also [greatly reduced](https://github.com/ProjectMapK/jackson-module-kogera/pull/44).

[Here](./docs/FixedIssues.md) is a list of issues that are not resolved in `jackson-module-kotlin` but are or will be resolved in `kogera`.

## About intentional destructive changes
This project makes several disruptive changes to achieve more `Kotlin-like` behavior.  
Details are summarized in [KogeraSpecificImplementations](./docs/KogeraSpecificImplementations.md).

# Compatibility
- `jackson 2.19.x`
- `Java 17+`
- `Kotlin 2.0.21+`

## About compatibility checks
Compatibility checks for `Java` and `Kotlin` are done by `CI` grid tests.

The `Java` test covers all currently supported LTS versions and the latest versions.

`Kotlin` is tested with the latest patch version and the latest `Beta` or `RC` version within each minor version after the minimum version.  
The `Kotlin` version is the lowest version available.

See [Workflow](./.github/workflows/test-main.yml) for the currently tested versions.

# Installation
The package is temporarily published in `JitPack`.  
Please refer to `jitpack.io` for the released version.

[ProjectMapK / jackson\-module\-kogera](https://jitpack.io/#ProjectMapK/jackson-module-kogera)

```kotlin
repositories {
    // ...

    maven { setUrl("https://jitpack.io") }
}

dependencies {
  // ...

  implementation("com.github.ProjectMapK:jackson-module-kogera:${version}")
}
```

## Migration in existing projects
When replacing `jackson-module-kotlin` in an existing project, please follow these steps

1. Replace the dependencies of `jackson-module-kotlin` with `jackson-module-kogera`.
2. Replace `com.fasterxml.jackson.module.kotlin` used for `import` with `io.github.projectmapk.jackson.module.kogera`.

In projects that use `spring-boot`, there is a possibility that `jackson-module-kotlin` will be used by auto-configuration.  
Therefore, it is necessary to exclude `jackson-module-kotlin` from the dependencies and to configure it manually.

You can check if your project's dependencies include `jackson-module-kotlin` by looking at `. /gradlew dependencies`.

If you find any problems, it would be appreciated if you could share them in an `issue`.

# About the future
I will not be treating this project as "stable".

This project currently serves as a testing ground for all improvements to `jackson-module-kotlin`.  
In prototyping, it is important to be willing to make disruptive changes.

# About license
This project is based on `jackson-module-kotlin`, so the license follows `jackson-module-kotlin`.  
The current license is `Apache License 2.0`.

[jackson\-module\-kotlin/LICENSE at 2\.14 · FasterXML/jackson\-module\-kotlin](https://github.com/FasterXML/jackson-module-kotlin/blob/2.14/LICENSE)

# About `Kogera`
`Kogera` is the Japanese name for `Japanese pygmy woodpecker`.  
This bird is the smallest woodpecker in Japan.  
