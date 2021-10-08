plugins {
    id("org.jetbrains.compose") version "1.0.0-alpha4-build396"
    id("com.android.application")
    kotlin("android")
}

group = "moe.tlaster"
version = "1.0"

dependencies {
    implementation(project(":sample:common"))
    implementation("androidx.activity:activity-compose:1.4.0-beta01")
}

android {
    compileSdkVersion(31)
    defaultConfig {
        applicationId = "moe.tlaster.android"
        minSdkVersion(31)
        targetSdkVersion(31)
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