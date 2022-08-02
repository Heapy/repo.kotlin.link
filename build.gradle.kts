plugins {
    kotlin("jvm").version("1.7.10")
    kotlin("plugin.serialization").version("1.7.10")
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
    implementation("io.undertow:undertow-core:2.2.17.Final")
    implementation("ch.qos.logback:logback-classic:1.2.11")
    implementation("io.micrometer:micrometer-registry-prometheus:1.9.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.8.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
}
