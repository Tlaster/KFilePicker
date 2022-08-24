import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.2.0-alpha01-dev770"
    id("com.android.application")
}

kotlin {
    ios("uikit") {
        binaries {
            executable {
                entryPoint = "moe.tlaster.kfilepicker.main"
                freeCompilerArgs += listOf(
                    "-linker-option", "-framework", "-linker-option", "Metal",
                    "-linker-option", "-framework", "-linker-option", "CoreText",
                    "-linker-option", "-framework", "-linker-option", "CoreGraphics",
                    "-linker-option", "-lsqlite3"
                )
            }
        }
    }
    android()
    macosX64 {
        binaries {
            executable {
                entryPoint = "moe.tlaster.kfilepicker.main"
                freeCompilerArgs += listOf(
                    "-linker-option",
                    "-framework",
                    "-linker-option",
                    "Metal",
                    "-linker-option",
                    "-lsqlite3"
                )
            }
        }
    }
    macosArm64 {
        binaries {
            executable {
                entryPoint = "moe.tlaster.kfilepicker.main"
                freeCompilerArgs += listOf(
                    "-linker-option",
                    "-framework",
                    "-linker-option",
                    "Metal",
                    "-linker-option",
                    "-lsqlite3"
                )
            }
        }
    }

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.ui)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                api(project(":KFilePicker"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("androidx.appcompat:appcompat:1.5.0")
                implementation("androidx.activity:activity-compose:1.6.0-beta01")
            }
        }
        val darwinMain by creating {
            dependsOn(commonMain)
            dependencies {
            }
        }
        val uikitMain by getting {
            dependsOn(darwinMain)
            dependencies {
            }
        }
        val macosMain by creating {
            dependsOn(darwinMain)
            dependencies {
            }
        }
        val macosX64Main by getting {
            dependsOn(macosMain)
        }
        val macosArm64Main by getting {
            dependsOn(macosMain)
        }
    }

    targets.withType<KotlinNativeTarget> {
        binaries.all {
            // TODO: the current compose binary surprises LLVM, so disable checks for now.
            freeCompilerArgs += "-Xdisable-phases=VerifyBitcode"
            binaryOptions["memoryModel"] = "experimental"
        }
    }
}

compose {
    desktop {
        application {
            mainClass = "moe.tlaster.kfilepicker.MainKt"
            nativeDistributions {
                targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
                packageName = "kfilepicker"
                packageVersion = "1.0.0"
                macOS {
                    bundleID = "moe.tlaster.kfilepicker"
                    // iconFile.set(project.file("src/jvmMain/resources/icon/ic_launcher.icns"))
                }
                linux {
                    // iconFile.set(project.file("src/jvmMain/resources/icon/ic_launcher.png"))
                }
                windows {
                    shortcut = true
                    menu = true
                    // iconFile.set(project.file("src/jvmMain/resources/icon/ic_launcher.ico"))
                }
            }
        }
        nativeApplication {
            targets(kotlin.targets.getByName("macosX64"), kotlin.targets.getByName("macosArm64"))
            distributions {
                targetFormats(TargetFormat.Dmg)
                packageName = "kfilepicker"
                packageVersion = "1.0.0"
                macOS {
                    bundleID = "moe.tlaster.kfilepicker"
                    // iconFile.set(project.file("src/jvmMain/resources/icon/ic_launcher.icns"))
                }
            }
        }
    }
    experimental {
        uikit {
            application {
                bundleIdPrefix = "moe.tlaster"
                projectName = "kfilepicker"
                deployConfigurations {
                    simulator("Simulator") {
                        device = org.jetbrains.compose.experimental.dsl.IOSDevices.IPHONE_13_MINI
                    }
                }
            }
        }
    }
}

android {
    namespace = "moe.tlaster.kfilepicker"

    compileSdkVersion(33)
    defaultConfig {
        applicationId = "moe.tlaster.kfilepicker"
        minSdkVersion(29)
        targetSdkVersion(33)
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

val runJvm by tasks.registering {
    dependsOn("runDistributable")
}

val runIos by tasks.registering {
    dependsOn("iosDeploySimulatorDebug")
}

if (rootProject.file("signing.properties").exists()) {
    val installIos by tasks.registering {
        dependsOn("iosDeployDeviceRelease")
    }
}

if (System.getProperty("os.arch") == "aarch64") {
    val runMacos by tasks.registering {
        dependsOn("runDebugExecutableMacosArm64")
    }
    val runMacosRelease by tasks.registering {
        dependsOn("runReleaseExecutableMacosArm64")
    }
} else {
    val runMacos by tasks.registering {
        dependsOn("runDebugExecutableMacosX64")
    }
    val runMacosRelease by tasks.registering {
        dependsOn("runReleaseExecutableMacosX64")
    }
}
