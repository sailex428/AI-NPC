plugins {
    id("fabric-loom") version "1.8-SNAPSHOT"
}

group = rootProject.extra["mod.group"] as String
version = rootProject.extra["mod.version"] as String

var mcVersion = property("mc.version").toString()
var fabricLoaderVersion = property("deps.fabric_loader").toString()

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    flatDir {
        dirs("libs")
        dirs("../../libs")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    mappings("net.fabricmc:yarn:$mcVersion+build.${property("deps.yarn_build")}:v2")
    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    include(modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fapi")}+$mcVersion")!!)

    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    include(modImplementation("org.xerial:sqlite-jdbc:3.46.1.3")!!)
    include(modImplementation("cabaletta:baritone-api-fabric:${property("deps.baritone")}")!!)
    include(modRuntimeOnly("dev_babbaj:nether-pathfinder-1.4.1")!!)

    include(modImplementation("io.github.ollama4j:ollama4j:1.0.89")!!)

    //needed deps for openai communication
    include(modRuntimeOnly("com.fasterxml.jackson.core:jackson-core:2.18.1")!!)
    include(modRuntimeOnly("com.fasterxml.jackson.core:jackson-annotations:2.18.1")!!)
    include(modRuntimeOnly("com.fasterxml.jackson.core:jackson-databind:2.18.1")!!)
    include(modRuntimeOnly("com.fasterxml:classmate:1.7.0")!!)
    include(modRuntimeOnly("com.github.victools:jsonschema-generator:4.36.0")!!)
    include(modRuntimeOnly("com.github.victools:jsonschema-module-jackson:4.36.0")!!)
    include(modRuntimeOnly("io.github.sashirestela:slimvalidator:1.2.2")!!)
    include(modRuntimeOnly("io.github.sashirestela:cleverclient:1.4.4")!!)

    include(modImplementation("io.github.sashirestela:simple-openai:3.9.0") {
        exclude(group = "org.slf4j")
    })
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
