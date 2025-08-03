package io.rndev.loginlab.feature.core

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Login : NavKey

@Serializable
data object Register : NavKey

@Serializable
data class Verify(val verificationId: String, val phoneNumber: String) : NavKey

@Serializable
data object Home : NavKey