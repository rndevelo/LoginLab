package io.rndev.loginlab.verify

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import io.rndev.loginlab.R
import io.rndev.loginlab.composables.LoadingAnimation
import io.rndev.loginlab.login.composables.PhoneOptionContent
import kotlinx.serialization.Serializable

@Serializable
data class Verify(val verificationId: String) : NavKey

@Composable
fun VerifyScreen(
    vm: VerifyViewModel = hiltViewModel(),
    verificationId: String,
    onHome: () -> Unit,
    onBack: () -> Unit,
) {

    val state = vm.uiState.collectAsState()
    val isLoggedIn = state.value.isLoggedIn
    val error = state.value.error
    val snackBarHostState = remember { SnackbarHostState() }
    var otpCode by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn == true) {
            onHome()
        }
    }

    LaunchedEffect(error) {
        if (error != null) {
            snackBarHostState.showSnackbar(error)
            vm.onClearError()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .imePadding()  // 👈 añade padding automático cuando aparece el teclado
            .navigationBarsPadding(),
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .animateContentSize()
                .verticalScroll(rememberScrollState()), // 👈 permite hacer scroll si el teclado tapa
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            PhoneOptionContent(
                title = stringResource(R.string.login_text_enter_sms_code),
                label = stringResource(R.string.login_text_code),
                initialValue = otpCode,
                textButton = stringResource(R.string.login_text_verify_code),
                isEnabled = otpCode.length == 6,
                error = error,
                leadingIconContent = {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = Icons.Default.PhoneAndroid.toString()
                    )
                },
                onInitialValue = { otpCode = it.take(6) },
                onClick = {
                    vm.onVerifyPhoneNumberWithCode(
                        verificationId = verificationId,
                        otpCode = otpCode
                    )
                },
                onBack = onBack
            )
        }
        if (state.value.isLoading == true) LoadingAnimation()
    }
}
