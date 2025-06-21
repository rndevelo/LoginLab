package io.rndev.loginlab.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable

@Serializable
data object Home

@Composable
fun HomeScreen(vm: HomeViewModel = viewModel()) {
//    val state = vm.state

    Button(
        onClick = { vm.onSignOut() },
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
    ) {
        Text("Sign out")
    }
}