package io.rndev.loginlab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import io.rndev.loginlab.ui.theme.LoginLabTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginLabTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Navigation()
                        Text(
                            text = "100% Android Best Practices by Rndev",
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
}
