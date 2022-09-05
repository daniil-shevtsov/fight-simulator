buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven(uri("https://plugins.gradle.org/m2/")) // For kotlinter-gradle
    }

    dependencies {
        // keeping this here to allow AS to automatically update
        classpath("com.android.tools.build:gradle:7.0.3")

        with(Deps.Gradle) {
//            classpath(composeMultiplatform)
            classpath(kotlin)
            classpath(kotlinSerialization)
            classpath(shadow)
            classpath(kotlinter)
            classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.5")
        }
    }
}

allprojects {
    apply(plugin = "org.jmailen.kotlinter")

    repositories {
        google()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers")
        maven(url = "https://jitpack.io")
        maven(url = "https://dl.bintray.com/korlibs/korlibs")
    }
}