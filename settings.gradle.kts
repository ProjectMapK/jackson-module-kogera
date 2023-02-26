rootProject.name = "jackson-module-kogera"

dependencyResolutionManagement {
    versionCatalogs {
        System.getenv("KOTLIN_VERSION")?.let { kotlinVersion ->
            println("Using kotlin version: $kotlinVersion")
            for (catalog in this) {
                catalog.version("kotlin", kotlinVersion)
            }
        }
    }
}
