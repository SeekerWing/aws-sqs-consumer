import org.gradle.api.tasks.bundling.Jar
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        jcenter()
    }
}

group = "io.github.seekerwing"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.3.31"
    id("io.gitlab.arturbosch.detekt") version "1.0.0-RC15"
    id("org.jlleitschuh.gradle.ktlint") version "8.1.0"
    id("org.jetbrains.dokka") version "0.9.18"
    jacoco
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0-M1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.3.0-M1")
    implementation("software.amazon.awssdk:sqs:2.5.64")
    implementation("org.apache.logging.log4j:log4j-api-kotlin:1.0.0")
    implementation("org.apache.logging.log4j:log4j-api:2.11.2")
    implementation("org.apache.logging.log4j:log4j-core:2.11.2")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.0-M1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
    testImplementation("io.mockk:mockk:1.9.3")
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

jacoco {
    toolVersion = "0.8.4"
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
                minimum = "0.85".toBigDecimal()
            }
            limit {
                counter = "BRANCH"
                minimum = "1.00".toBigDecimal()
            }
            limit {
                counter = "COMPLEXITY"
                minimum = "0.82".toBigDecimal()
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

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "aws-sqs-consumer"

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
                name.set("aws-sqs-consumer")
                description.set("The AWS SQS Consumer reduces time to launch a SQS Message Consumer by empowering developers to focus on business logic of processing the message.")
                url.set("https://seekerwing.github.io/aws-sqs-consumer/")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("baruray")
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
    repositories {
        maven {
            // https://issues.sonatype.org/browse/OSSRH-50005
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

            val ossrhUsername: String by project
            val ossrhPassword: String by project
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
