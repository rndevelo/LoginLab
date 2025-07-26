package io.rndev.loginlab.feature.core.composables

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LoadingAnimation(color: Color = MaterialTheme.colorScheme.onPrimary) {
    CircularProgressIndicator(
        modifier = Modifier.size(28.dp),
        color = color,
    )
}