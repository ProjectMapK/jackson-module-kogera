rootProject.name = "jackson-module-kogera"

dependencyResolutionManagement {
    versionCatalogs {
        // gradle/libs.versions.toml is imported automatically, so only the overrides are declared here.
        create("libs") {
            // Mainly for CI, they can be rewritten by environment variables.
            System.getenv("KOTLIN_VERSION")?.takeIf { it.isNotEmpty() }?.let { version("kotlin", it) }
            System.getenv("JACKSON_VERSION")?.takeIf { it.isNotEmpty() }?.let { version("jackson", it) }
        }
    }
}
