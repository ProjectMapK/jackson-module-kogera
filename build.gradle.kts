plugins {
    `maven-publish` // for JitPack
    kotlin("jvm") version "1.7.21"
    java
    id("org.jmailen.kotlinter") version "3.13.0"
}

val jacksonVersion = "2.14.2"

group = "com.fasterxml.jackson"
version = "${jacksonVersion}-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.6.0")

    api("com.fasterxml.jackson.core:jackson-databind:${jacksonVersion}")
    api("com.fasterxml.jackson.core:jackson-annotations:${jacksonVersion}")

    // test libs
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("io.mockk:mockk:1.13.3")

    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
}

kotlin {
    explicitApi()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlinter {
    // see https://github.com/pinterest/ktlint/blob/master/docs/rules/standard.md
    this.disabledRules = arrayOf(
        "package-name", // This project allows for the inclusion of _ to represent the package name in the snake case.
        "filename" // For clarity in future extensions, this rule is disabled.
    )
}

tasks {
    compileKotlin {
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
