// Enterprise-Grade Root Build Configuration
// Following Meta, Google, and Amazon best practices

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false

    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.4.2" apply false

    alias(libs.plugins.google.firebase.crashlytics) apply false

    // JaCoCo for code coverage
    id("jacoco") apply false
}

subprojects {
    // Common task configurations
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs += listOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
            )
        }
    }

    // JaCoCo configuration for all modules
    plugins.withId("jacoco") {
        tasks.withType<Test> {
            extensions.configure<JacocoTaskExtension> {
                isIncludeNoLocationClasses = true
                excludes = listOf("jdk.internal.*")
            }
            finalizedBy("jacocoTestReport")
        }
        tasks.register<JacocoReport>("jacocoTestReport") {
            dependsOn(tasks.withType<Test>())
            reports {
                xml.required.set(false)
                csv.required.set(false)
                html.required.set(true)
                html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
            }
            classDirectories.setFrom(
                files(classDirectories.files.map {
                    fileTree(it) {
                        exclude(
                            "**/R.class",
                            "**/R$*.class",
                            "**/BuildConfig.*",
                            "**/Manifest*.*",
                            "**/*Test*.*"
                        )
                    }
                })
            )
        }
    }

    // Android common configuration for application modules
    plugins.withId("com.android.application") {
        extensions.configure<com.android.build.gradle.AppExtension> {
            compileSdkVersion(35)

            defaultConfig {
                minSdk = 24
                targetSdk = 35
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                vectorDrawables.useSupportLibrary = true
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }

            testOptions {
                unitTests.isReturnDefaultValues = true
                unitTests.isIncludeAndroidResources = true
            }

            packagingOptions {
                resources.excludes += setOf(
                    "META-INF/AL2.0",
                    "META-INF/LGPL2.1",
                    "META-INF/LICENSE.md",
                    "META-INF/LICENSE-notice.md"
                )
            }
        }
    }

    // Android common configuration for library modules
    plugins.withId("com.android.library") {
        extensions.configure<com.android.build.gradle.LibraryExtension> {
            compileSdk = 35

            defaultConfig {
                minSdk = 24
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                consumerProguardFiles("consumer-rules.pro")
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }

            testOptions {
                unitTests.isReturnDefaultValues = true
                unitTests.isIncludeAndroidResources = true
            }
        }
    }
}

// Global tasks for enterprise operations
tasks.register("cleanAll") {
    description = "Clean all modules"
    group = "enterprise"

    dependsOn(gradle.includedBuilds.map { it.task(":clean") })
    dependsOn(subprojects.map { "${it.path}:clean" })
}

tasks.register("testAll") {
    description = "Run all tests across all modules"
    group = "enterprise"

    dependsOn(subprojects.map { "${it.path}:test" })
}

tasks.register("generateDependencyReport") {
    description = "Generate dependency report for all modules"
    group = "enterprise"

    doLast {
        val reportFile = file("$buildDir/reports/dependencies/all-dependencies.txt")
        reportFile.parentFile.mkdirs()

        reportFile.writeText("# Rooster App - Dependency Report\n")
        reportFile.appendText("Generated: ${java.time.LocalDateTime.now()}\n\n")

        subprojects.forEach { project ->
            reportFile.appendText("## ${project.name}\n")
            project.configurations.forEach { config ->
                if (config.isCanBeResolved) {
                    try {
                        config.resolvedConfiguration.resolvedArtifacts.forEach { artifact ->
                            reportFile.appendText("- ${artifact.moduleVersion.id}\n")
                        }
                    } catch (e: Exception) {
                        // Skip unresolvable configurations
                    }
                }
            }
            reportFile.appendText("\n")
        }

        println("Dependency report generated: ${reportFile.absolutePath}")
    }
}
