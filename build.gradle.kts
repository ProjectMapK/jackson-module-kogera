plugins {
    `maven-publish` // for JitPack

    val kotlinVersion: String = System.getenv("KOTLIN_VERSION")?.takeIf { it.isNotEmpty() } ?: "1.7.21"
    kotlin("jvm") version kotlinVersion
    id("org.jetbrains.dokka") version "1.8.10"

    java
    id("org.jmailen.kotlinter") version "3.13.0"
}

// Since group cannot be obtained by generateKogeraVersion, it is defined as a constant.
val groupStr = "io.github.projectmapk"
val jacksonVersion = "2.15.1"
val generatedSrcPath = "$buildDir/generated/kotlin"

group = groupStr
version = "${jacksonVersion}-beta0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.6.0")

    api("com.fasterxml.jackson.core:jackson-databind:${jacksonVersion}")
    api("com.fasterxml.jackson.core:jackson-annotations:${jacksonVersion}")

    // test libs
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("io.mockk:mockk:1.13.3")

    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
}

kotlin {
    explicitApi()
    // for PackageVersion
    sourceSets["main"].apply {
        kotlin.srcDir(generatedSrcPath)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withSourcesJar()
    withJavadocJar()
}

kotlinter {
    // see https://github.com/pinterest/ktlint/blob/master/docs/rules/standard.md
    this.disabledRules = arrayOf(
        "package-name", // This project allows for the inclusion of _ to represent the package name in the snake case.
        "filename" // For clarity in future extensions, this rule is disabled.
    )
}

tasks {
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

    test {
        useJUnitPlatform()
    }

    // This task is added by Gradle when we use java.withJavadocJar()
    val javadocJar = named<Jar>("javadocJar") {
        from(named("dokkaJavadoc"))
    }
    // The project currently has no Java files to publish, and the javadoc task is disabled to avoid errors.
    javadoc {
        isEnabled = false
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
