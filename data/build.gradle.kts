plugins {
    id("rndev.android.library")
    id("rndev.di.library")
}

android {
    namespace = "io.rndev.loginlab.data"
}

dependencies {
    implementation(project(":domain"))
    testImplementation(project(":test:unit"))
}
