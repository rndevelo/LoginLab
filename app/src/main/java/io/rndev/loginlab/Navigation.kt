package io.rndev.loginlab

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import io.rndev.loginlab.data.AuthRepositoryImpl
import io.rndev.loginlab.home.Home
import io.rndev.loginlab.home.HomeScreen
import io.rndev.loginlab.login.Login
import io.rndev.loginlab.login.LoginScreen
import io.rndev.loginlab.login.LoginViewModel
import io.rndev.loginlab.register.Register
import io.rndev.loginlab.register.RegisterScreen
import io.rndev.loginlab.register.RegisterViewModel

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun Navigation() {

    val backStack = remember { mutableStateListOf<Any>(Login) }

    val authViewModel = AuthViewModel(
        authRepository = AuthRepositoryImpl(auth = Firebase.auth),
    )

    val loginViewModel = LoginViewModel(
        authRepository = AuthRepositoryImpl(auth = Firebase.auth),
    )

    val registerViewModel = RegisterViewModel(
        authRepository = AuthRepositoryImpl(auth = Firebase.auth),
    )

    val isAuthenticated = authViewModel.isAuthenticated.collectAsState()

    LaunchedEffect(isAuthenticated.value) {
        Log.d("isAuthenticated", "Navigation: ${isAuthenticated.value}")
        if (isAuthenticated.value is Result.Success) {
            backStack.add(Home)
        }
    }

    NavDisplay(
        backStack = backStack
    ) { key ->
        when (key) {
            Login -> NavEntry(key) {
                LoginScreen(
                    vm = loginViewModel,
                    onRegister = { backStack.add(Register) },
                    onHome = { backStack.add(Home) }
                )
            }

            Register -> NavEntry(key) {
                RegisterScreen(
                    vm = registerViewModel,
                    onHome = { backStack.add(Home) },
                    onBack = { backStack.removeLastOrNull() })
            }

            Home -> NavEntry(key) {
                HomeScreen()
            }

            else -> error("No route for $key")
        }


    }
}