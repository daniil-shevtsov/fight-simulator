plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("com.android.library")
}

//setupMultiplatform()

android {
    compileSdk = Versions.androidCompileSdk
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = Versions.androidMinSdk
        targetSdk = Versions.androidTargetSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}


kotlin {
    android()
    jvm()
    js(IR) {
        browser()
    }

    sourceSets {
        all {
            languageSettings.useExperimentalAnnotation("kotlin.time.ExperimentalTime")
        }

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.1")
                implementation(Deps.Kotlinx.coroutinesCore)
//                implementation(compose.web.widgets)
//                implementation(compose.runtime)

                with(Deps.Koin) {
                    api(core)
                }

                with(Deps.KorLibs) {
                    implementation(kBigNum)
                }
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(Deps.Kotlinx.coroutinesCore)
                with(Deps.UnitTest) {
                    implementation(assertk)
                    implementation(turbine)
                }
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Deps.Kotlinx.coroutinesCore)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(Deps.Kotlinx.coroutinesCore)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(Deps.Kotlinx.coroutinesCore)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(Deps.Kotlinx.coroutinesCore)

                implementation(kotlin("test-junit"))
                with(Deps.UnitTest) {
                    implementation(coroutinesTest)
                    implementation(mockk)
                    implementation(turbine)
                }
            }
        }

        val jsMain by getting {

        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"

        freeCompilerArgs = listOf(
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xopt-in=kotlinx.coroutines.time",
            "-Xopt-in=kotlin.Experimental",
            "-Xopt-in=kotlin.time.ExperimentalTime",
            "-Xopt-in=androidx.compose.foundation.ExperimentalFoundationApi",
        )


    }
}
