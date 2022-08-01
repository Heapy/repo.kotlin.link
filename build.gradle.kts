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
    implementation("org.apache.httpcomponents.client5:httpclient5:5.2-beta1")
    implementation("io.micrometer:micrometer-registry-prometheus:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
}
