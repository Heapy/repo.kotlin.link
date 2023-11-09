plugins {
    kotlin("jvm").version("1.9.20")
    kotlin("plugin.serialization").version("1.9.20")
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
    implementation("io.undertow:undertow-core:2.3.10.Final")
    implementation("ch.qos.logback:logback-classic:1.4.11")
    implementation("io.micrometer:micrometer-registry-prometheus:1.11.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.9.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
}

kotlin {
    sourceSets.all {
        languageSettings {
            languageVersion = "2.0"
        }
    }
}

