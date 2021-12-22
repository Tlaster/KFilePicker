pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    
}
rootProject.name = "KFilePicker"

include(":KFilePicker")
include(":sample:android")
include(":sample:desktop")
include(":sample:common")
include(":sample:web")