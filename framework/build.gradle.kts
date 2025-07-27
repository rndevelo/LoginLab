plugins {
    id("rndev.android.library")
    id("rndev.di.library")
    id("rndev.firebase.library")
}

android {
    namespace = "io.rndev.loginlab.framework"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))
}
