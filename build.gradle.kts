plugins {
    kotlin("jvm").version("1.4.32")
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
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.undertow:undertow-core:2.2.7.Final")
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation("io.micrometer:micrometer-registry-prometheus:1.6.6")

    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.3")
}
