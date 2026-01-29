import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestResult
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.jvm.toolchain.JavaToolchainService

import java.util.Properties
import java.io.FileInputStream

plugins {
    `java-library`
    `maven-publish`
    signing
    id("com.gradleup.nmcp") version "1.4.3"
    id("com.diffplug.spotless") version "8.2.1"
}

// Load .env file if it exists
val envFile = file(".env")
if (envFile.exists()) {
    val props = Properties()
    FileInputStream(envFile).use { props.load(it) }
    props.forEach { key, value ->
        var strValue = value.toString().trim()
        if (strValue.startsWith("\"") && strValue.endsWith("\"") && strValue.length >= 2) {
            strValue = strValue.substring(1, strValue.length - 1)
        } else if (strValue.startsWith("'") && strValue.endsWith("'") && strValue.length >= 2) {
            strValue = strValue.substring(1, strValue.length - 1)
        }

        // Remove 'APPLE_MAPS_TOKEN=' prefix if mistakenly included in the value
        if (strValue.startsWith("APPLE_MAPS_TOKEN=")) {
            strValue = strValue.substring("APPLE_MAPS_TOKEN=".length)
        }

        System.setProperty(key.toString(), strValue)
    }
}

val javaTargetVersion = 17

group = providers.gradleProperty("GROUP").orNull ?: "com.williamcallahan"
version = providers.gradleProperty("version").orNull
    ?: providers.gradleProperty("VERSION_NAME").orNull
    ?: "0.1.5"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaTargetVersion))
    }
    withSourcesJar()
    withJavadocJar()
}

val javaToolchainService = extensions.getByType<JavaToolchainService>()

val javaLauncherForTargetVersion = javaToolchainService.launcherFor {
    languageVersion.set(JavaLanguageVersion.of(javaTargetVersion))
}

dependencies {
    implementation(platform("tools.jackson:jackson-bom:3.0.4"))
    implementation("tools.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.0.2")
}

spotless {
    java {
        target("src/**/*.java")
        trimTrailingWhitespace()
        endWithNewline()
    }
    format("misc") {
        target("*.gradle.kts", "*.md", ".gitignore", "Makefile")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(javaTargetVersion)
    options.compilerArgs.add("-Xlint:unchecked")
    options.compilerArgs.add("-Xlint:deprecation")
}

val integrationTestPattern = "*IT"

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    javaLauncher.set(javaLauncherForTargetVersion)

    // Pass APPLE_MAPS_TOKEN to tests
    val token = System.getenv("APPLE_MAPS_TOKEN") ?: System.getProperty("APPLE_MAPS_TOKEN")
    if (token != null) {
        environment("APPLE_MAPS_TOKEN", token)
    }
}

tasks.register<Test>("testDetail") {
    description = "Runs integration tests with detailed logging."
    group = "verification"
    val testSourceSet = sourceSets["test"]
    testClassesDirs = testSourceSet.output.classesDirs
    classpath = testSourceSet.runtimeClasspath
    filter {
        includeTestsMatching(integrationTestPattern)
    }
    testLogging {
        events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        exceptionFormat = TestExceptionFormat.FULL
        showStandardStreams = true
    }
    addTestListener(object : TestListener {
        override fun beforeSuite(suite: TestDescriptor) = Unit

        override fun beforeTest(testDescriptor: TestDescriptor) = Unit

        override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {
            val testClassName = testDescriptor.className ?: testDescriptor.name
            logger.lifecycle("TEST ${result.resultType}: ${testClassName}.${testDescriptor.name}")
        }

        override fun afterSuite(suite: TestDescriptor, result: TestResult) {
            if (suite.parent == null) {
                logger.lifecycle(
                    "TEST SUMMARY: ${result.resultType} (" +
                        "${result.testCount} tests, " +
                        "${result.successfulTestCount} passed, " +
                        "${result.failedTestCount} failed, " +
                        "${result.skippedTestCount} skipped)"
                )
            }
        }
    })
}

tasks.withType<JavaExec>().configureEach {
    javaLauncher.set(javaLauncherForTargetVersion)
}

tasks.named("check") {
    dependsOn("spotlessCheck")
}

tasks.register<JavaExec>("cli") {
    description = "Runs the Apple Maps CLI against the live API."
    group = "application"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.williamcallahan.applemaps.cli.AppleMapsCli")

    val token = System.getenv("APPLE_MAPS_TOKEN") ?: System.getProperty("APPLE_MAPS_TOKEN")
    if (token != null) {
        environment("APPLE_MAPS_TOKEN", token)
    }

    val userLocation = System.getenv("APPLE_MAPS_USER_LOCATION") ?: System.getProperty("APPLE_MAPS_USER_LOCATION")
    if (userLocation != null) {
        environment("APPLE_MAPS_USER_LOCATION", userLocation)
    }

    val userLocationQuery = System.getenv("APPLE_MAPS_USER_LOCATION_QUERY") ?: System.getProperty("APPLE_MAPS_USER_LOCATION_QUERY")
    if (userLocationQuery != null) {
        environment("APPLE_MAPS_USER_LOCATION_QUERY", userLocationQuery)
    }
}

tasks.withType<Javadoc>().configureEach {
    (options as StandardJavadocDocletOptions).addStringOption("-release", javaTargetVersion.toString())
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = providers.gradleProperty("POM_ARTIFACT_ID").orNull ?: "apple-maps-java"

            pom {
                name.set(providers.gradleProperty("POM_NAME").orNull ?: "Apple Maps Java")
                description.set(providers.gradleProperty("POM_DESCRIPTION").orNull ?: "Apple Maps Java implements the Apple Maps Server API for use in JVMs.")
                url.set(providers.gradleProperty("POM_URL").orNull ?: "https://github.com/WilliamAGH/apple-maps-java")

                licenses {
                    license {
                        name.set(providers.gradleProperty("POM_LICENSE_NAME").orNull ?: "MIT License")
                        url.set(providers.gradleProperty("POM_LICENSE_URL").orNull ?: "https://opensource.org/license/mit")
                    }
                }

                developers {
                    developer {
                        id.set(providers.gradleProperty("POM_DEVELOPER_ID").orNull ?: "WilliamAGH")
                        name.set(providers.gradleProperty("POM_DEVELOPER_NAME").orNull ?: "William Callahan")
                        url.set(providers.gradleProperty("POM_DEVELOPER_URL").orNull ?: "https://github.com/WilliamAGH/")
                    }
                }

                scm {
                    url.set(providers.gradleProperty("POM_SCM_URL").orNull ?: "https://github.com/WilliamAGH/apple-maps-java")
                    connection.set(providers.gradleProperty("POM_SCM_CONNECTION").orNull ?: "scm:git:git://github.com/WilliamAGH/apple-maps-java.git")
                    developerConnection.set(providers.gradleProperty("POM_SCM_DEV_CONNECTION").orNull ?: "scm:git:ssh://git@github.com/WilliamAGH/apple-maps-java.git")
                }
            }
        }
    }

    repositories {
        maven {
            val releasesUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsUrl else releasesUrl

            credentials {
                username = providers.environmentVariable("SONATYPE_USERNAME").orNull
                password = providers.environmentVariable("SONATYPE_PASSWORD").orNull
            }
        }
    }
}

signing {
    val signingKey = providers.environmentVariable("GPG_PRIVATE_KEY").orNull
    val signingPassword = providers.environmentVariable("GPG_PASSPHRASE").orNull

    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    }
}

nmcp {
    publishAllPublicationsToCentralPortal {
        username.set(providers.environmentVariable("SONATYPE_USERNAME").orNull)
        password.set(providers.environmentVariable("SONATYPE_PASSWORD").orNull)
        publishingType.set("USER_MANAGED")
    }
}
