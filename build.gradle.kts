import com.xpdustry.toxopid.spec.ModMetadata

plugins {
    id("com.diffplug.spotless") version "6.25.0"
    id("org.jetbrains.dokka") version "1.9.20"
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.3.0"
    id("net.kyori.indra") version "3.1.3"
    id("net.kyori.indra.git") version "3.1.3"
    id("net.kyori.indra.publishing.gradle-plugin") version "3.1.3"
    `kotlin-dsl`
    id("com.Eri-Neko.catali") version "1.0"
}

group = "com.xpdustry"
version = "4.1.1" + if (indraGit.headTag() == null) "-SNAPSHOT" else ""
description = "Gradle plugin for building and testing mindustry mods/plugins."

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    compileOnly(gradleApi())
    implementation("org.hjson:hjson:3.1.0")
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
}

spotless {
    kotlin {
        ktlint()
        licenseHeaderFile(rootProject.file("HEADER.txt"))
    }
    kotlinGradle {
        ktlint()
    }
}

indra {
    javaVersions {
        target(17)
        minimumToolchain(17)
    }

    publishSnapshotsTo("xpdustry", "https://maven.xpdustry.com/snapshots")
    publishReleasesTo("xpdustry", "https://maven.xpdustry.com/releases")

    mitLicense()

    github("xpdustry", "toxopid") {
        ci(true)
        issues(true)
        scm(true)
    }

    configurePublications {
        pom {
            organization {
                name = "Xpdustry"
                url = "https://www.xpdustry.com"
            }

            developers {
                developer {
                    id = "Phinner"
                    timezone = "Europe/Brussels"
                }
            }
        }
    }
}

kotlin {
    explicitApi()
}

indraPluginPublishing {
    website("https://github.com/xpdustry/toxopid")

    plugin(
        "toxopid",
        "com.xpdustry.toxopid.plugin.ToxopidPlugin",
        "Toxopid",
        project.description,
        listOf("mindustry", "testing"),
    )
}

val metadata = ModMetadata.fromJson(project.file("plugin.json"))
// Setting the project version from the one located in "mod.json"
project.version = metadata.version


tasks.javadocJar {
    from(tasks.dokkaHtml)
}
