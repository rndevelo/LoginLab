plugins {
    id("rndev.android.library")
}

android {
    namespace = "io.rndev.loginlab.domain"
}

dependencies {
    implementation (libs.facebook.login)
}
