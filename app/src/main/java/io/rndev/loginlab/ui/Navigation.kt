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
import io.rndev.loginlab.feature.auth.login.LoginScreen
import io.rndev.loginlab.feature.auth.register.RegisterScreen
import io.rndev.loginlab.feature.auth.verify.VerifyScreen
import io.rndev.loginlab.feature.auth.verify.VerifyViewModel
import io.rndev.loginlab.feature.core.Home
import io.rndev.loginlab.feature.core.Login
import io.rndev.loginlab.feature.core.Register
import io.rndev.loginlab.feature.core.Verify
import io.rndev.loginlab.home.HomeScreen

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