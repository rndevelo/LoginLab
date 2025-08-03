import com.android.build.api.dsl.ApplicationExtension
import io.rndev.loginlab.addAndroidTestDependencies
import io.rndev.loginlab.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")

            val extension = extensions.getByType<ApplicationExtension>()
            configureAndroidCompose(extension)

            addAndroidTestDependencies()
        }
    }
}