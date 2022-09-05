plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.0.0-beta5"
}

repositories {
    mavenCentral()
    maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    js(IR) {
        browser {
            useCommonJs()
            binaries.executable()
        }
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project(":common"))

                implementation(compose.web.core)
                implementation(compose.runtime)
            }
        }
    }
}

//kotlin {
//    js(IR) {
//        browser {
//            useCommonJs()
//            binaries.executable()
//        }
//    }
//
//    sourceSets {
//        val jsMain by getting {
//            dependencies {
//                implementation(Deps.Kotlinx.coroutinesCore)
//
//                implementation(compose.web.core)
//                implementation(compose.runtime)
//
//                with(Deps.Koin) {
//                    api(core)
//                }
//
//                with(Deps.KorLibs) {
//                    implementation(kBigNum)
//                }
//            }
//        }
//    }
//}