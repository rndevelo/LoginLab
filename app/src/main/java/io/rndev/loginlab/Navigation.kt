package io.rndev.loginlab

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import io.rndev.loginlab.home.Home
import io.rndev.loginlab.home.HomeScreen
import io.rndev.loginlab.login.Login
import io.rndev.loginlab.login.LoginScreen
import io.rndev.loginlab.register.Register
import io.rndev.loginlab.register.RegisterScreen
import io.rndev.loginlab.splash.Splash
import io.rndev.loginlab.splash.SplashScreen

@Composable
fun Navigation() {

    val backStack = rememberNavBackStack(Splash)

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<Splash> {
                SplashScreen(
                    onLogin = {
                        backStack.clear()
                        backStack.add(Login)
                    },
                    onHome = {
                        backStack.clear()
                        backStack.add(Home)
                    },
                    onRetry = {
                        backStack.clear()
                        backStack.add(Splash)
                    }
                )
            }
            entry<Login> {
                LoginScreen(
                    onRegister = { backStack.add(Register) },
                    onHome = {
                        backStack.clear()     // 🔄 Limpia toda la pila
                        backStack.add(Home)
                    }
                )
            }
            entry<Register> {
                RegisterScreen(
                    onHome = { backStack.add(Home) },
                    onBack = { backStack.removeLastOrNull() }
                )
            }
            entry<Home> {
                HomeScreen {
                    backStack.clear()
                    backStack.add(Login)
                }
            }
        }
    )
}