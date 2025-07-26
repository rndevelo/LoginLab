package io.rndev.loginlab

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import io.rndev.loginlab.ui.Navigation
import io.rndev.loginlab.ui.theme.LoginLabTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
//        setupCustomExitAnimation(splashScreen)
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            mainViewModel.isAuthenticated.value == null // Mantener splash mientras isReady es nulo
        }

        enableEdgeToEdge()
        setContent {

            LoginLabTheme {
                val isReady by mainViewModel.isAuthenticated.collectAsState()

                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        isReady?.let {
                            Navigation(isReady = it)
                        }
                        Text(
                            text = stringResource(R.string.app_text_100_android_best_practices_by_rndev),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 32.dp)
                        )
                    }
                }
            }
        }
    }

    private fun setupCustomExitAnimation(splashScreen: SplashScreen) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->

                val iconView = splashScreenViewProvider.iconView
                val splashScreenView = splashScreenViewProvider.view

                // 1. Animación del icono: Escalar y Desvanecer
                val iconScaleX = ObjectAnimator.ofFloat(iconView, View.SCALE_X, 1f, 1.5f, 0.5f, 0f)
                val iconScaleY = ObjectAnimator.ofFloat(iconView, View.SCALE_Y, 1f, 1.5f, 0.5f, 0f)
                val iconAlpha = ObjectAnimator.ofFloat(iconView, View.ALPHA, 1f, 0f)

                val iconAnimatorSet = AnimatorSet().apply {
                    interpolator = AnticipateInterpolator(1.5f) // Un poco de anticipación
                    duration = 400L // Duración para la animación del icono
                    playTogether(iconScaleX, iconScaleY, iconAlpha)
                }

                // 2. Animación de la vista de la splash screen: Desvanecer
                val splashViewAlpha = ObjectAnimator.ofFloat(splashScreenView, View.ALPHA, 1f, 0f).apply {
                    interpolator = AccelerateDecelerateInterpolator()
                    duration = 300L // Un poco más rápido o igual que el icono
                }

                // 3. Combinar y ejecutar
                val finalAnimatorSet = AnimatorSet()
                finalAnimatorSet.playSequentially(
                    iconAnimatorSet, // Primero anima el icono
                    splashViewAlpha  // Luego (o casi al mismo tiempo) desvanece la pantalla
                    // Para que se ejecuten más en paralelo, usa playTogether
                    // o ajusta los startDelays
                )
                // Para una superposición más suave:
                // finalAnimatorSet.playTogether(iconAnimatorSet, splashViewAlpha)
                // splashViewAlpha.startDelay = 100L // Que la pantalla empiece a desvanecerse un poco después del icono


                finalAnimatorSet.doOnEnd {
                    splashScreenViewProvider.remove() // ¡MUY IMPORTANTE!
                }
                finalAnimatorSet.start()
            }
        }
    }
}

