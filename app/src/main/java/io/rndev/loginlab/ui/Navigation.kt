package io.rndev.loginlab.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import io.rndev.loginlab.ui.home.Home
import io.rndev.loginlab.ui.home.HomeScreen
import io.rndev.loginlab.ui.login.Login
import io.rndev.loginlab.ui.login.LoginScreen
import io.rndev.loginlab.ui.register.Register
import io.rndev.loginlab.ui.register.RegisterScreen
import io.rndev.loginlab.ui.verify.Verify
import io.rndev.loginlab.ui.verify.VerifyScreen
import io.rndev.loginlab.ui.verify.VerifyViewModel

@Composable
fun Navigation(isReady: Boolean) {

    val backStack = rememberNavBackStack(if (isReady) Home else Login)

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<Login> {
                LoginScreen(
                    onNavigate = { navKey ->
                        backStack.clear()
                        backStack.add(navKey)
                    }
                )
            }
            entry<Register> {
                RegisterScreen(
                    onHome = { backStack.add(Home) },
                    onBack = { backStack.removeLastOrNull() }
                )
            }
            entry<Verify> { key ->
                val viewModel = hiltViewModel<VerifyViewModel, VerifyViewModel.Factory>(
                    creationCallback = { factory ->
                        factory.create(key)
                    }
                )
                VerifyScreen(
                    vm = viewModel,
                    onHome = { backStack.add(Home) },
                    onBack = { backStack.removeLastOrNull() },
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