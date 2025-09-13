import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `maven-publish` // for JitPack

    val kotlinVersion: String = System.getenv("KOTLIN_VERSION")?.takeIf { it.isNotEmpty() }
        ?: libs.versions.kotlin.get()

    kotlin("jvm") version kotlinVersion

    java
    alias(libs.plugins.kotlinter)
}

// Since group cannot be obtained by generateKogeraVersion, it is defined as a constant.
val groupStr = "io.github.projectmapk"
val jacksonVersion = libs.versions.jackson.get()
val generatedSrcPath = "${layout.buildDirectory.get()}/generated/kotlin"

group = groupStr
version = "${jacksonVersion}-beta30"

repositories {
    mavenCentral()
}

dependencies {
    val kotlinVersion: String = System.getenv("KOTLIN_VERSION")?.takeIf { it.isNotEmpty() }
        ?: libs.versions.kotlin.get()
    implementation("${libs.kotlin.stdlib.get()}:${kotlinVersion}")
    implementation("${libs.kotlin.metadata.jvm.get()}:${kotlinVersion}")

    implementation(platform(libs.jackson.bom))
    api(libs.jackson.databind)
    api(libs.jackson.annotations)

    // test libs
    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation(libs.mockk)

    testImplementation("${libs.kotlin.reflect.get()}:${kotlinVersion}")
    testImplementation(libs.jackson.xml)
    testImplementation(libs.jackson.csv)
    testImplementation(libs.jackson.jsr310)
}

kotlin {
    explicitApi()
    // for PackageVersion
    sourceSets["main"].apply {
        kotlin.srcDir(generatedSrcPath)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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

    // Added to avoid failure in generating dependency graphs in CI.
    lintKotlinMain {
        dependsOn.add(generateKogeraVersion)
    }

    // For ported tests, they are excluded from the formatting because they are not new code.
    lintKotlinTest {
        exclude { it.path.contains("zPorted") }
    }
    formatKotlinTest {
        exclude { it.path.contains("zPorted") }
    }

    compileKotlin {
        dependsOn.add(generateKogeraVersion)
        compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
    }

    compileTestKotlin {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
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
