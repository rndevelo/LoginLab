package io.rndev.loginlab.feature.auth.register

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.rndev.loginlab.feature.auth.UiEvent
import io.rndev.loginlab.feature.core.R
import io.rndev.loginlab.feature.core.composables.EmailOptionContent
import io.rndev.loginlab.feature.core.composables.PasswordTextField

@Composable
fun RegisterScreen(
    vm: RegisterViewModel = hiltViewModel(),
    onHome: () -> Unit,
    onBack: () -> Unit
) {

    val state = vm.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

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
            .imePadding()                          // 👈 añade padding automático cuando aparece el teclado
            .navigationBarsPadding(),
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .animateContentSize()
                .verticalScroll(rememberScrollState()), // 👈 permite hacer scroll si el teclado tapa
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            EmailOptionContent(
                isLoading = state.value.isLoading == true,
                title = stringResource(R.string.login_text_sign_up_with_email),
                email = state.value.email,
                emailError = state.value.emailError,
                password = state.value.password,
                localError = state.value.localError,
                onEmailValueChange = vm::onEmailValueChange,
                textButton = stringResource(R.string.login_text_sign_up),
                onBack = onBack,
                onClick = vm::onValidate,
                firstPasswordTextField = {

                    PasswordTextField(
                        value = state.value.password,
                        passwordError = state.value.passwordError,
                        localError = state.value.localError,
                        imeAction = ImeAction.Next,
                        onValueChange = { vm.onPasswordValueChange(it) },
                    )
                },
                secondPasswordTextField = {

                    Spacer(Modifier.height(8.dp))
                    PasswordTextField(
                        value = state.value.confirmPassword,
                        confirmPasswordError = state.value.confirmPasswordError,
                        localError = state.value.localError,
                        imeAction = ImeAction.Done,
                        keyboardActions = KeyboardActions { vm.onValidate() },
                        onValueChange = { vm.onConfirmPasswordValueChange(it) },
                    )
                }
            )
        }

        AnimatedVisibility(visible = state.value.isEmailSent == true) {
            EmailVerificationDialog(
                isVerified = state.value.isEmailVerified == true,
                onCompleted = vm::onNavigateToHomeEvent
            )
        }
    }
}

@Composable
fun EmailVerificationDialog(
    isVerified: Boolean,
    onCompleted: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {},
        title = if (!isVerified) {
            {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.login_text_email_sent),
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }
        } else null,

        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isVerified) {
                    // Diseño simple y elegante para el estado verificado
                    SimpleCheckWithProgressAnimation(
                        modifier = Modifier.size(100.dp),
                        onCompleted = onCompleted
                    )
                } else {
                    // Texto para pedir que revise el email
                    Text(
                        text = stringResource(R.string.login_text_check_your_email_to_verify_your_account),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    )
}

@Composable
fun SimpleCheckWithProgressAnimation(
    modifier: Modifier = Modifier,
    onCompleted: () -> Unit = {}
) {
    val progress = remember { Animatable(0f) }
    val scale = remember { Animatable(0f) }
    var checkVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // 1. Círculo se dibuja
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = LinearEasing)
        )

        // 2. Cuando termina el círculo → mostrar el check
        checkVisible = true

        // 3. Escala de aparición y rebote
        scale.animateTo(1.3f, tween(durationMillis = 150))
        scale.animateTo(1f, tween(durationMillis = 100))

        onCompleted()
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Círculo progresivo
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = Color(0xFF4CAF50),
                startAngle = -90f,
                sweepAngle = 360f * progress.value,
                useCenter = false,
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Solo aparece el check cuando el progreso se completa
        if (checkVisible) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Check",
                tint = Color(0xFF4CAF50),
                modifier = Modifier
                    .size(54.dp)
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                    }
            )
        }
    }
}