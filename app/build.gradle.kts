import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    id("rndev.android.application")
    id("rndev.android.application.compose")
    id("rndev.di.library.compose")
}

android {
    namespace = "io.rndev.loginlab"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.rndev.loginlab"
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").readText().byteInputStream())

        val webGoogleIDClient = properties.getProperty("WEB_GOOGLE_ID_CLIENT", "")
        buildConfigField("String", "WEB_GOOGLE_ID_CLIENT", "\"$webGoogleIDClient\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":feature:core"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:user"))
    implementation(project(":framework"))

    implementation(libs.androidx.activity.compose)

//    Splash
    implementation(libs.androidx.core.splashscreen)

//    Navigation3
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.material3.navigation3)

//    Serialization
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}