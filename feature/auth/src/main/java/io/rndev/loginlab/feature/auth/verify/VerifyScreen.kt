package io.rndev.loginlab.feature.auth.verify

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
import io.rndev.loginlab.feature.auth.UiEvent
import io.rndev.loginlab.feature.core.R
import io.rndev.loginlab.feature.core.composables.PhoneOptionContent

@Composable
fun VerifyScreen(
    vm: VerifyViewModel = hiltViewModel(),
    onHome: () -> Unit,
    onBack: () -> Unit,
) {

    val state = vm.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    var otpCode by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        vm.events.collect { event ->
            when (event) {
                is UiEvent.NavigateToHome -> onHome()
                is UiEvent.ShowError -> snackBarHostState.showSnackbar(event.message)
                is UiEvent.NavigateToVerification -> TODO()
                is UiEvent.NavigateToLogin -> TODO()
            }
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
                isLoading = state.value.isLoading == true,
                title = stringResource(R.string.login_text_enter_sms_code),
                label = stringResource(R.string.login_text_code),
                initialValue = otpCode,
                textButton = stringResource(R.string.login_text_verify_code),
                isEnabled = otpCode.length == 6,
                error = state.value.errorMessage,
                leadingIconContent = {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = Icons.Default.PhoneAndroid.toString()
                    )
                },
                onInitialValue = { otpCode = it.take(6) },
                onClick = { vm.onVerifyPhoneNumberWithCode(otpCode = otpCode) },
                onBack = onBack,
            )
        }
    }
}
