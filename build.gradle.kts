import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

plugins {
    kotlin("multiplatform") version "1.8.21"
    kotlin("plugin.serialization") version "1.8.21"
    id("org.jetbrains.dokka") version "1.9.20"
    id("publication-plugin")
    `maven-publish`
}

group = "io.github.vincentvibe3"
version = "1.0.7"

repositories {
    mavenCentral()
}

val isCI: Boolean = System.getenv("CI").toBoolean()

kotlin {
    val publicationsFromMainHost =
        listOf(jvm(), linuxX64()).map { target: KotlinTarget ->  target.name } + "kotlinMultiplatform"
    publishing{
        publications{
            if (isCI){
                matching {
                    it.name in publicationsFromMainHost
                }.all{
                    val targetPublication = this@all
                    tasks.withType<AbstractPublishToMaven>()
                        .matching { it.publication == targetPublication }
                        .configureEach { onlyIf { System.getProperty("os.name") == "Linux" } }
                }
            }
        }
    }

    jvm {
        jvmToolchain(11)
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    iosSimulatorArm64{
        binaries {
            framework {
                baseName = "library"
            }
        }
    }
    iosArm64 {
        binaries {
            framework {
                baseName = "library"
            }
        }
    }
    iosX64 {
        binaries {
            framework {
                baseName = "library"
            }
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    macosX64()
    macosArm64()
    linuxX64()
    mingwX64()

    sourceSets {
        val ktorVersion = "2.2.4"
        val serializationVersion = "1.5.1"
        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.ktor:ktor-client-mock:$ktorVersion")
            }
        }


        val macosX64Test by getting {
            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")
            }
        }
        val macosArm64Test by getting {
            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")
            }
        }

        val linuxX64Test by getting {
            dependencies {
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
            }
        }

        val mingwX64Test by getting {
            dependencies {
                implementation("io.ktor:ktor-client-winhttp:$ktorVersion")
            }
        }



        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
            }
        }
        val iosArm64Main by getting
        val iosArm64Test by getting
        val iosX64Main by getting
        val iosX64Test by getting
        val iosSimulatorArm64Main by getting
        val iosSimulatorArm64Test by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosTest by creating {
            dependsOn(commonMain)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")
            }
        }
    }

}

tasks.dokkaHtml.configure {
    outputDirectory.set(project.projectDir.resolve("docs"))
    dokkaSourceSets {
        configureEach {
            jdkVersion.set(17)
        }
    }
}

tasks.dokkaJavadoc.configure {
    outputDirectory.set(buildDir.resolve("dokka/javadoc"))
    dokkaSourceSets {
        configureEach {
            jdkVersion.set(17)
        }
    }
}