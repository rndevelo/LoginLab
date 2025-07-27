plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication"){
            id = "rndev.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "rndev.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidFeature") {
            id = "rndev.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidLibrary") {
            id = "rndev.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "rndev.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("jvmLibrary") {
            id = "rndev.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("diLibrary") {
            id = "rndev.di.library"
            implementationClass = "DiLibraryConventionPlugin"
        }
        register("diLibraryCompose") {
            id = "rndev.di.library.compose"
            implementationClass = "DiLibraryComposeConventionPlugin"
        }
        register("firebaseLibrary") {
            id = "rndev.firebase.library"
            implementationClass = "FirebaseLibraryConventionPlugin"
        }
    }
}