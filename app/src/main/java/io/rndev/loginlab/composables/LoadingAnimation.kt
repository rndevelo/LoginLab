package io.rndev.loginlab.composables

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun LoadingAnimation() {
    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
}