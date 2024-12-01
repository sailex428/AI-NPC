pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.kikugie.dev/snapshots")
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.5-beta.5"
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    create(projects = listOf("ai-npc-client", "ai-npc-launcher")) {
        versions("1.20.4", "1.21.1", "1.21.3")
        vcsVersion = "1.20.4"
    }
}

rootProject.name = "ai-npc"