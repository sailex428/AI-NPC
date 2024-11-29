plugins {
    id("com.diffplug.spotless") version "7.0.0.BETA2"
    id("java")
}

subprojects {
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "java")

    tasks.register("prepareKotlinBuildScriptModel") {}

    spotless {
        java {
            palantirJavaFormat()
            indentWithTabs()
            removeUnusedImports()
            trimTrailingWhitespace()
            endWithNewline()
        }
    }
}
