plugins {
    `maven-publish` // for JitPack

    val kotlinVersion: String = System.getenv("KOTLIN_VERSION")?.takeIf { it.isNotEmpty() } ?: "1.8.22"
    kotlin("jvm") version kotlinVersion

    java
    id("org.jmailen.kotlinter") version "3.16.0"
}

// Since group cannot be obtained by generateKogeraVersion, it is defined as a constant.
val groupStr = "io.github.projectmapk"
val jacksonVersion = "2.16.0"
val generatedSrcPath = "${layout.buildDirectory.get()}/generated/kotlin"

group = groupStr
version = "${jacksonVersion}-beta7"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.7.0")

    api("com.fasterxml.jackson.core:jackson-databind:${jacksonVersion}")
    api("com.fasterxml.jackson.core:jackson-annotations:${jacksonVersion}")

    // test libs
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("io.mockk:mockk:1.13.7")

    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
    testImplementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
}

kotlin {
    explicitApi()
    // for PackageVersion
    sourceSets["main"].apply {
        kotlin.srcDir(generatedSrcPath)
    }

    val useK2 = System.getenv("KOTLIN_VERSION")?.takeIf { it.isNotEmpty() }
        ?.let { it.toBoolean() } ?: false

    sourceSets.all {
        languageSettings {
            if (useK2) {
                languageVersion = "2.0"
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    // For ported tests, they are excluded from the formatting because they are not new code.
    lintKotlinTest {
        exclude { it.path.contains("zPorted") }
    }
    formatKotlinTest {
        exclude { it.path.contains("zPorted") }
    }

    // Task to generate version file
    val generateKogeraVersion by registering(Copy::class) {
        val packageStr = "$groupStr.jackson.module.kogera"

        from(
            resources.text.fromString(
                """
package $packageStr

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.core.util.VersionUtil

public val kogeraVersion: Version = VersionUtil.parseVersion("$version", "$groupStr", "${rootProject.name}")

                """.trimIndent()
            )
        ) {
            rename { "KogeraVersion.kt" }
        }

        into(file("$generatedSrcPath/${packageStr.replace(".", "/")}"))
    }

    compileKotlin {
        dependsOn.add(generateKogeraVersion)
        kotlinOptions.jvmTarget = "1.8"
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    test {
        useJUnitPlatform()
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
