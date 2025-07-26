
import com.android.build.gradle.LibraryExtension
import io.rndev.loginlab.addAndroidTestDependencies
import io.rndev.loginlab.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("rndev.android.library.compose")
            }

            extensions.configure<LibraryExtension> {
                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }
            }

            dependencies {
                add("implementation", project(":feature:core"))
                add("implementation", libs.findLibrary("androidx.lifecycle.viewmodel.compose").get())

//                add("testImplementation", project(":test:unit"))
//                add("testImplementation", libs.findLibrary("kotlinx.coroutines.test").get())
//                add("testImplementation", libs.findLibrary("turbine").get())
            }

//            addAndroidTestDependencies()
        }
    }
}