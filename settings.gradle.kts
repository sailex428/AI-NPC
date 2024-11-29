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

    shared {
        versions("1.20.4", "1.21.3")
        vcsVersion = "1.20.4"
    }
    create(project = ("ai-npc-client"))
    create(project = ("ai-npc-launcher"))
}

rootProject.name = "ai-npc"
include("ai-npc-client", "ai-npc-launcher")