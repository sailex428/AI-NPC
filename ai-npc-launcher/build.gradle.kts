plugins {
    id("fabric-loom") version "1.8-SNAPSHOT"
}

group = rootProject.extra["mod.group"] as String
version = rootProject.extra["mod.version"] as String

var mcVersion = property("mc.version").toString()
var fabricLoaderVersion = property("deps.fabric_loader").toString()

repositories {
    flatDir {
        dirs("../../libs")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    mappings("net.fabricmc:yarn:${mcVersion}+build.${property("deps.yarn_build")}:v2")
    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    include(modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fapi")}+$mcVersion")!!)

    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    include(modImplementation("me.earth.headlessmc:headlessmc-launcher-repackaged:2.3.0")!!)
}

loom {
    splitEnvironmentSourceSets()

    mods {
        create("ai-npc-launcher") {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }
}

tasks.processResources {
    inputs.property("version", version)
    inputs.property("mcDep", mcVersion)
    inputs.property("loader_version", fabricLoaderVersion)
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to version,
            "mcDep" to mcVersion,
            "loader_version" to fabricLoaderVersion
        )
    }
}

java {
    withSourcesJar()
    val java = if (stonecutter.eval(mcVersion, ">=1.20.6")) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    targetCompatibility = java
    sourceCompatibility = java
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/$version"))
    dependsOn("build")
}

tasks.register<Jar>("repackageHeadlessmc") {
    group = "project"
    from(zipTree("libs/headlessmc-launcher-2.3.0.jar")) {
        exclude("org/objectweb/asm/**")
    }
    destinationDirectory.set(file("libs"))
    archiveFileName.set("headlessmc-launcher-repackaged-2.3.0.jar")
}
