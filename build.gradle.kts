plugins {
    kotlin("jvm").version("1.9.23")
    kotlin("plugin.serialization").version("1.9.23")
    application
}

application {
    mainClass.set("link.kotlin.repo.ApplicationKt")
    applicationName = "repo"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.undertow:undertow-core:2.3.12.Final")
    implementation("ch.qos.logback:logback-classic:1.5.3")
    implementation("io.micrometer:micrometer-registry-prometheus:1.12.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.11.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
}

kotlin {
    sourceSets.all {
        languageSettings {
            languageVersion = "2.0"
        }
    }
}

