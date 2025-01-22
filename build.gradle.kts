plugins {
    kotlin("jvm").version("2.1.0")
    kotlin("plugin.serialization").version("2.1.0")
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
    implementation("io.undertow:undertow-core:2.3.18.Final")
    implementation("ch.qos.logback:logback-classic:1.5.16")
    implementation("io.micrometer:micrometer-registry-prometheus:1.14.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.12.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
}
