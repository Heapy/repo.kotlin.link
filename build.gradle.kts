plugins {
    kotlin("jvm").version("1.4.21")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.undertow:undertow-core:2.2.3.Final")
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("org.apache.httpcomponents:httpclient:4.5.13")

    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.11.2")
}
