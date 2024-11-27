plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "1.20.4" /* [SC] DO NOT EDIT */
stonecutter.automaticPlatformConstants = true

stonecutter registerChiseled tasks.register("chiseledBuildAll", stonecutter.chiseled) {
    group = "build"
    ofTask("buildBothMods")
}

tasks.register("buildBothMods") {
    group = "build"
    description = "Builds both subprojects for the current Minecraft version"
    dependsOn(":ai-npc-client:build", ":ai-npc-launcher:build")
}