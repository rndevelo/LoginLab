plugins {
    id("rndev.android.library")
}

android {
    namespace = "io.rndev.loginlab.test.unit"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(libs.junit)
    implementation(libs.kotlinx.coroutines.test)
}
