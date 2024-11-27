pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.kikugie.dev/snapshots")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.5-beta.5"
}

stonecutter {
    centralScript = "build.gradle.kts"
    kotlinController = true

    create(rootProject) {
        versions("1.20.4", "1.21")
        vcsVersion = "1.20.4"
    }
}

rootProject.name = "ai-npc"
include("ai-npc-client", "ai-npc-launcher")