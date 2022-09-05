object Versions {
    const val androidMinSdk = 21
    const val androidCompileSdk = 31
    const val androidTargetSdk = androidCompileSdk

    const val assertk = "0.24"

    const val accompanist = "0.20.2"

    const val compose = "1.0.5"
    const val composeMultiplatform = "1.0.0-alpha08"

    const val dagger = "2.36"

    const val jupiter = "5.7.2"
    const val junit = "4.13"
    const val mockk = "1.11.0"

    const val kBigNum = "2.2.0"

    const val kotlin = "1.5.31"
    const val kotlinCoroutines = "1.5.2-native-mt"
    const val koin = "3.1.1"
    const val kotlinterGradle = "3.4.5"

    const val material = "1.4.0"

    const val navCompose = "2.4.0-alpha04"
    const val navigationComponent = "2.3.5"

    const val shadow = "7.0.0"

    const val timber = "5.0.1"
    const val turbine = "0.6.1"

    const val viewBindingDelegate = "1.4.6"
}

object Deps {
    object Gradle {
//        const val composeMultiplatform =
//            "org.jetbrains.compose:compose-gradle-plugin:${Versions.composeMultiplatform}"

        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        const val kotlinSerialization =
            "org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}"
        const val kotlinter = "org.jmailen.gradle:kotlinter-gradle:${Versions.kotlinterGradle}"
        const val shadow =
            "gradle.plugin.com.github.jengelman.gradle.plugins:shadow:${Versions.shadow}"
    }

    object Android {
        const val material = "com.google.android.material:material:${Versions.material}"
    }

    object Accompanist {
        const val insets = "com.google.accompanist:accompanist-insets:${Versions.accompanist}"
        const val pager = "com.google.accompanist:accompanist-pager:${Versions.accompanist}"
    }

    object Compose {
        const val ui = "androidx.compose.ui:ui:${Versions.compose}"
        const val uiGraphics = "androidx.compose.ui:ui-graphics:${Versions.compose}"
        const val uiTooling = "androidx.compose.ui:ui-tooling:${Versions.compose}"
        const val foundationLayout =
            "androidx.compose.foundation:foundation-layout:${Versions.compose}"
        const val material = "androidx.compose.material:material:${Versions.compose}"
        const val materialExtended = "androidx.compose.material:material-icons-extended:${Versions.compose}"
        const val navigation = "androidx.navigation:navigation-compose:${Versions.navCompose}"
    }

    object Koin {
        const val core = "io.insert-koin:koin-core:${Versions.koin}"
        const val android = "io.insert-koin:koin-android:${Versions.koin}"
        const val compose = "io.insert-koin:koin-androidx-compose:${Versions.koin}"
    }

    object KorLibs {
        const val kBigNum = "com.soywiz.korlibs.kbignum:kbignum:${Versions.kBigNum}"
        const val kBigNumAndroid = "com.soywiz.korlibs.kbignum:kbignum-android:${Versions.kBigNum}"
    }

    object Kotlinx {
        const val coroutinesCore =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}"
    }

    object Dagger {
        const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
        const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
    }

    object Logging {
        const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    }

    object NavigationComponent {
        const val uiKtx = "androidx.navigation:navigation-ui-ktx:${Versions.navigationComponent}"
        const val fragmentKtx =
            "androidx.navigation:navigation-fragment-ktx:${Versions.navigationComponent}"
    }

    object ViewBinding {
        const val delegate =
            "com.github.kirich1409:viewbindingpropertydelegate:${Versions.viewBindingDelegate}"
    }

    object UnitTest {
        const val assertk = "com.willowtreeapps.assertk:assertk:${Versions.assertk}"

        const val jupiter = "org.junit.jupiter:junit-jupiter:${Versions.jupiter}"
        const val junit = "junit:junit:${Versions.junit}"

        const val mockk = "io.mockk:mockk:${Versions.mockk}"
        const val mockkCommon = "io.mockk:mockk-common:${Versions.mockk}"
        const val mockkAgent = "io.mockk:mockk-agent-jvm:${Versions.mockk}"

        const val turbine = "app.cash.turbine:turbine:${Versions.turbine}"
        const val coroutinesTest =
            "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.kotlinCoroutines}"
    }

}
