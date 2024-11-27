plugins {
    id("com.diffplug.spotless") version "7.0.0.BETA2"
}

subprojects {
    apply(plugin = "com.diffplug.spotless")

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
