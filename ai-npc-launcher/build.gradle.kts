plugins {
    id("fabric-loom") version "1.8-SNAPSHOT"
}

group = rootProject.extra["mod.group"] as String
version = rootProject.extra["mod.version"] as String
val mcVersion = "1.20.4"
val fabricLoaderVersion = "0.15.11"
val fabricApiVersion = "0.97.2+1.20.4"
val yarnVersion = "1.20.4+build.3"

repositories {
    flatDir {
        dirs("libs")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    mappings("net.fabricmc:yarn:$yarnVersion:v2")
    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    include(modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")!!)
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    include(modImplementation("me.earth.headlessmc:headlessmc-launcher-repackaged:2.3.0")!!)
}

tasks.register<Jar>("repackageHeadlessmc") {
    from(zipTree("libs/headlessmc-launcher-2.3.0.jar")) {
        exclude("org/objectweb/asm/**")
    }
    destinationDirectory.set(file("libs"))
    archiveFileName.set("headlessmc-launcher-repackaged-2.3.0.jar")
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

val targetJavaVersion = 17

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
            options.release.set(targetJavaVersion)
        }
    }
}

java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
    withSourcesJar()
}

val archivesBaseName: String by project

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${archivesBaseName}" }
    }
}