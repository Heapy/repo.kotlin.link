plugins {
    kotlin("jvm").version("1.9.0")
    kotlin("plugin.serialization").version("1.9.0")
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
    implementation("io.undertow:undertow-core:2.3.8.Final")
    implementation("ch.qos.logback:logback-classic:1.4.11")
    implementation("io.micrometer:micrometer-registry-prometheus:1.11.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.9.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}
