package io.rndev.loginlab.register

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import io.rndev.loginlab.R
import io.rndev.loginlab.UiEvent
import io.rndev.loginlab.composables.LoadingAnimation
import io.rndev.loginlab.login.composables.EmailOptionContent
import io.rndev.loginlab.login.composables.PasswordTextField
import kotlinx.serialization.Serializable

@Serializable
data object Register : NavKey

@Composable
fun RegisterScreen(
    vm: RegisterViewModel = hiltViewModel(),
    onHome: () -> Unit,
    onBack: () -> Unit
) {

    val state = vm.uiState.collectAsState()
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        vm.events.collect { event ->
            when (event) {
                is UiEvent.NavigateToHome -> onHome()
                is UiEvent.ShowError -> snackBarHostState.showSnackbar(event.message)
                is UiEvent.NavigateToVerification -> TODO()
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
                title = stringResource(R.string.login_text_sign_up_with_email),
                textButton = stringResource(R.string.login_text_sign_up),
                onBack = onBack,
                onSign = vm::onSignUp,
                passwordTextField = { isPasswordValid, localError, onLocalError ->

                    PasswordTextField(
                        value = confirmPassword,
                        isPasswordValid = isPasswordValid,
                        localError = localError,
                        imeAction = ImeAction.Next,
                        onValueChange = {
                            confirmPassword = it
                            onLocalError()
                        },
                    )

                    Spacer(Modifier.height(8.dp))
                }
            )
        }
        if (state.value.isLoading == true) LoadingAnimation()

        AnimatedVisibility(state.value.isEmailSent == true) {
            EmailVerificationDialog(isVerified = state.value.isEmailVerified == true)
        }
    }
}

@Composable
fun EmailVerificationDialog(isVerified: Boolean) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {},
        title = {
            Text(
                text = "Email Sent",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Check your email to verify your account.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (isVerified) {
                    LottieCheckAnimation(modifier = Modifier.size(120.dp))
                }
            }
        }
    )
}

@Composable
fun LottieCheckAnimation(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("email_verified.json"))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        iterations = 1
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}

@Composable
fun VerificationAnimation(isVerified: Boolean) {
    val checkScale = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(isVerified) {
        if (isVerified) {
            checkScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
            )
            rotation.animateTo(
                targetValue = 360f,
                animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
            )
        }
    }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
        // Girando círculo
        Canvas(modifier = Modifier.fillMaxSize()) {
            rotate(rotation.value) {
                drawArc(
                    color = Color(0xFF4CAF50),
                    startAngle = 0f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        // Icono check que escala
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Verified",
            tint = Color(0xFF4CAF50),
            modifier = Modifier
                .size(48.dp)
                .graphicsLayer {
                    scaleX = checkScale.value
                    scaleY = checkScale.value
                    alpha = checkScale.value
                }
        )
    }
}