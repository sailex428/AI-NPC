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
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    include(modImplementation("org.xerial:sqlite-jdbc:3.46.1.3")!!)
    include(modImplementation("cabaletta:baritone-api-fabric:1.10.2")!!)
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
