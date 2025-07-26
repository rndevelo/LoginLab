plugins {
    id("rndev.jvm.library")
    id("rndev.di.library")
}

dependencies {
    implementation(project(":domain"))
}
