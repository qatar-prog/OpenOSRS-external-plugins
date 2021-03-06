import ProjectVersions.openosrsVersion

buildscript {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    checkstyle
    java
    kotlin("jvm") version "1.3.71"
    id("com.simonharrer.modernizer") version "1.8.0-1" apply false
    id("com.github.ben-manes.versions") version "0.28.0"
    id("se.patrikerdes.use-latest-versions") version "0.2.13"
}

apply<BootstrapPlugin>()
apply<VersionPlugin>()

allprojects {
    repositories {
        mavenCentral {
            content {
                excludeGroupByRegex("com\\.openosrs.*")
            }
        }

        jcenter {
            content {
                excludeGroupByRegex("com\\.openosrs.*")
            }
        }

        exclusiveContent {
            forRepository {
                mavenLocal()
            }
            filter {
                includeGroupByRegex("com\\.openosrs.*")
            }
        }
    }
}

subprojects {
    group = "com.owain.externals"

    project.extra["PluginProvider"] = "Owain94"
    project.extra["ProjectUrl"] = "https://discord.gg/HVjnT6R"
    project.extra["PluginLicense"] = "3-Clause BSD License"

    apply<JavaPlugin>()
    apply(plugin = "checkstyle")
    apply(plugin = "kotlin")
    apply(plugin = "com.simonharrer.modernizer")

    dependencies {
        compileOnly(group = "com.openosrs", name = "http-api", version = "3.3.1")
        compileOnly(group = "com.openosrs", name = "runelite-api", version = "3.3.1")
        compileOnly(group = "com.openosrs", name = "runelite-client", version = "3.3.1")

        compileOnly(group = "org.apache.commons", name = "commons-text", version = "1.8")
        compileOnly(group = "com.google.inject", name = "guice", version = "4.2.3", classifier = "no_aop")
        compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.12")
        compileOnly(group = "org.pf4j", name = "pf4j", version = "3.3.1")

        // kotlin
        compileOnly(kotlin("stdlib"))
    }

    checkstyle {
        maxWarnings = 0
        toolVersion = "8.25"
        isShowViolations = true
        isIgnoreFailures = false
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
        }

        compileKotlin {
            kotlinOptions {
                jvmTarget = "11"
                freeCompilerArgs = listOf("-Xjvm-default=enable")
            }
            sourceCompatibility = "11"
        }

        withType<AbstractArchiveTask> {
            isPreserveFileTimestamps = false
            isReproducibleFileOrder = true
            dirMode = 493
            fileMode = 420
        }

        withType<Checkstyle> {
            group = "verification"
        }
    }
}

tasks {
    named<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask>("dependencyUpdates") {
        checkForGradleUpdate = false

        resolutionStrategy {
            componentSelection {
                all {
                    if (candidate.displayName.contains("fernflower") || isNonStable(candidate.version)) {
                        reject("Non stable")
                    }
                }
            }
        }
    }
}

fun isNonStable(version: String): Boolean {
    return listOf("ALPHA", "BETA", "RC").any {
        version.toUpperCase().contains(it)
    }
}