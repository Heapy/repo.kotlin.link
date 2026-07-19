plugins {
    kotlin("jvm").version("2.4.0")
    kotlin("plugin.serialization").version("2.4.0")
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
    implementation("io.undertow:undertow-core:2.4.2.Final")
    implementation("ch.qos.logback:logback-classic:1.5.38")
    implementation("io.micrometer:micrometer-registry-prometheus:1.17.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.12.0")
    testImplementation("org.junit.jupiter:junit-jupiter:6.1.2")
}
