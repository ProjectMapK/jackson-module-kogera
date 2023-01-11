plugins {
    kotlin("jvm") version "1.7.10"
    java
    id("org.jmailen.kotlinter") version "3.13.0"
}

group = "com.fasterxml.jackson"
version = "2.13.2-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.5.0")
    implementation(platform("com.fasterxml.jackson:jackson-bom:2.14.1"))

    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation("com.fasterxml.jackson.core:jackson-databind")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations
    implementation("com.fasterxml.jackson.core:jackson-annotations")

    // test libs
    testImplementation(kotlin("reflect"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    // https://mvnrepository.com/artifact/io.mockk/mockk
    testImplementation("io.mockk:mockk:1.12.5")

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
    test {
        useJUnitPlatform()
    }
}
