import io.rndev.loginlab.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class FirebaseLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {

            dependencies {
                add("implementation", libs.findLibrary("firebase.auth").get())
                add("implementation", libs.findLibrary("androidx.credentials").get())
                add("implementation", libs.findLibrary("androidx.credentials.play.services.auth").get())
                add("implementation", libs.findLibrary("googleid").get())
            }
        }
    }
}