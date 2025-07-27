plugins {
    id("rndev.android.feature")
    id("rndev.di.library.compose")
}

android {
    namespace = "io.rndev.loginlab.feature.auth"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":framework"))
    implementation (libs.facebook.login)
}
