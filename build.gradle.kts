import com.jfrog.bintray.gradle.BintrayExtension.PackageConfig
import com.jfrog.bintray.gradle.BintrayExtension.VersionConfig
import java.util.Date
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        jcenter()
    }
}

plugins {
    kotlin("jvm") version "1.3.71"
    id("io.gitlab.arturbosch.detekt") version "1.15.0"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.0"
    id("org.jetbrains.dokka") version "0.10.1"
    jacoco
    id("com.jfrog.bintray") version "1.8.5"
    `maven-publish`
}

group = "org.seekerwing"
version = "0.1"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2-native-mt")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.4.3-native-mt")
    implementation("software.amazon.awssdk:sqs:2.17.191")
    implementation("org.apache.logging.log4j:log4j-api-kotlin:1.0.0")
    implementation("org.apache.logging.log4j:log4j-api:2.14.0")
    implementation("org.apache.logging.log4j:log4j-core:2.14.0")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.3-native-mt")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
    testImplementation("io.mockk:mockk:1.10.5")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
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

jacoco {
    toolVersion = "0.8.5"
}

tasks.withType<JacocoReport> {
    reports {
        xml.isEnabled = true
        html.isEnabled = true
        xml.destination = file("$buildDir/reports/jacoco/jacocoTestReport.xml")
        html.destination = file("$buildDir/reports/jacoco")
    }
}

tasks.withType<JacocoCoverageVerification> {
    violationRules {
        rule {
            limit {
                counter = "LINE"
                minimum = "0.84".toBigDecimal()
            }
            limit {
                counter = "BRANCH"
                minimum = "1.00".toBigDecimal()
            }
            limit {
                counter = "COMPLEXITY"
                minimum = "0.79".toBigDecimal()
            }
        }
    }
}

tasks.withType<DokkaTask> {
    outputDirectory = "$buildDir/reports/javadoc"
}

tasks {
    named<Task>("check") {
        dependsOn(named<Task>("jacocoTestReport"))
        dependsOn(named<Task>("jacocoTestCoverageVerification"))
    }
    named<Task>("build") {
        dependsOn(named<Task>("dokka"))
        dependsOn(named<Task>("javadoc"))
    }
}

val projectName: String = "aws-sqs-consumer"

bintray {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_KEY")
    setPublications("main")
    publish = true
    override = true

    pkg(closureOf<PackageConfig> {
        repo = "maven"
        name = projectName
        vcsUrl = "https://github.com/SeekerWing/aws-sqs-consumer.git"
        version(closureOf<VersionConfig> {
            name = project.version.toString()
            desc = "AWS SQS Consumer"
            released = Date().toString()
        })
    })
}

publishing {
    publications {
        register("main", MavenPublication::class) {
            artifactId = projectName

            val sourcesJar by tasks.creating(Jar::class) {
                this.classifier = "sources"
                from(sourceSets["main"].allSource)
            }

            val javadocJar by tasks.creating(Jar::class) {
                this.classifier = "javadoc"
                from("$buildDir/reports/javadoc")
            }

            from(components["java"])
            artifact(sourcesJar)
            artifact(javadocJar)

            pom {
                name.set(projectName)
                description.set("The AWS SQS Consumer reduces time to launch a SQS Message Consumer by empowering " +
                        "developers to focus on business logic of processing the message.")
                url.set("https://seekerwing.github.io/aws-sqs-consumer/")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("SeekerWing")
                        name.set("Barun Ray")
                        email.set("ray.barunray@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/SeekerWing/aws-sqs-consumer.git")
                    developerConnection.set("scm:git:ssh://github.com/SeekerWing/aws-sqs-consumer.git")
                    url.set("https://github.com/SeekerWing/aws-sqs-consumer")
                }
            }
        }
    }
}
