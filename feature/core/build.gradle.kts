plugins {
    id("rndev.android.library.compose")
    id("rndev.di.library.compose")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "io.rndev.loginlab.feature.core"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}