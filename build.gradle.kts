plugins {
    `maven-publish` // for JitPack

    val kotlinVersion: String = System.getenv("KOTLIN_VERSION")?.takeIf { it.isNotEmpty() } ?: "1.5.32"
    kotlin("jvm") version kotlinVersion

    java
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
