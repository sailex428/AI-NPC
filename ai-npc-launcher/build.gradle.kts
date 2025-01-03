import me.modmuss50.mpp.ReleaseType
import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("fabric-loom")
    id("maven-publish")
    id("me.modmuss50.mod-publish-plugin")
}

version =  rootProject.extra["mod.version"] as String
var modVersion = rootProject.property("mod.version").toString()
var mcVersion = property("mc.version").toString()
var fabricLoaderVersion = property("deps.fabric_loader").toString()
var jarName = "ai-npc-launcher-$mcVersion-v$modVersion-fabric-beta"

val lombokVersion = "1.18.34"

repositories {
    flatDir {
        dirs("../../libs")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    mappings("net.fabricmc:yarn:${mcVersion}+build.${property("deps.yarn_build")}:v2")
    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fapi")}+$mcVersion")

    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    include(modImplementation("me.earth.headlessmc:headlessmc-launcher-repackaged:2.3.0")!!)
}

loom {
    runConfigs.all {
        ideConfigGenerated(true)
        runDir = "../../../server"
    }
}

java {
    withSourcesJar()
    val java = if (stonecutter.eval(mcVersion, ">=1.20.6")) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    targetCompatibility = java
    sourceCompatibility = java
}

tasks.jar {
    archiveVersion.set("")
}

tasks.remapJar {
    archiveBaseName.set(jarName)
    archiveVersion.set("")
}

tasks.remapSourcesJar {
    archiveBaseName.set(jarName)
    archiveVersion.set("")
}

tasks.processResources {
    inputs.property("version", modVersion)
    inputs.property("mcDep", mcVersion)
    inputs.property("loader_version", fabricLoaderVersion)
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to modVersion,
            "mcDep" to mcVersion,
            "loader_version" to fabricLoaderVersion
        )
    }
}

tasks.register<Jar>("repackageHeadlessmc") {
    description = "removes asm from headlessmc-launcher jar"
    group = "project"
    from(zipTree("../../libs/headlessmc-launcher-2.3.0.jar")) {
        exclude("org/objectweb/asm/**")
    }
    destinationDirectory.set(file("libs"))
    archiveFileName.set("headlessmc-launcher-repackaged-2.3.0.jar")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = rootProject.property("mod.group").toString()
            artifactId = property("archives_base_name").toString()
            version = "$modVersion+$mcVersion"

            artifact(tasks.remapJar.get().archiveFile)
            artifact(tasks.remapSourcesJar.get().archiveFile) {
                classifier = "sources"
            }
        }
    }
}

publishMods {
    file.set(tasks.named<RemapJarTask>("remapJar").get().archiveFile)
    changelog.set(rootProject.file("CHANGELOG.md").readText())
    modLoaders.add("fabric")
    type.set(ReleaseType.BETA)
    version.set(modVersion)

    github {
        accessToken.set(providers.gradleProperty("GITHUB_TOKEN"))
        parent(project(":").tasks.named("publishGithub"))
    }

    modrinth {
        displayName.set("v$modVersion [$mcVersion] AI NPC")
        accessToken.set(providers.gradleProperty("MODRINTH_TOKEN"))
        projectId.set(property("publish.modrinth").toString())
        minecraftVersions.add(mcVersion)
        requires("fabric-api")
    }
}
