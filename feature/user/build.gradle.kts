plugins {
    id("rndev.android.feature")
    id("rndev.di.library.compose")
}

android {
    namespace = "io.rndev.loginlab.feature.user"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))
}