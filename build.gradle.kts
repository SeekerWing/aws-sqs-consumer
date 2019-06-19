import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        jcenter()
    }
}

plugins {
    kotlin("jvm") version "1.3.31"
    id("io.gitlab.arturbosch.detekt") version "1.0.0-RC15"
    id("org.jlleitschuh.gradle.ktlint") version "8.1.0"
}

group = "org.seekerwing"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0-M1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.3.0-M1")
    implementation("software.amazon.awssdk:sqs:2.5.64")
    testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.1.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

detekt {
    config = files("config/detekt.yml")
}
