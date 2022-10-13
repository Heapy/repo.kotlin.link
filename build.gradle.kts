plugins {
    kotlin("jvm").version("1.7.20")
    kotlin("plugin.serialization").version("1.7.20")
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
    implementation("io.undertow:undertow-core:2.3.0.Final")
    implementation("ch.qos.logback:logback-classic:1.4.4")
    implementation("io.micrometer:micrometer-registry-prometheus:1.9.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.8.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.1")
}
