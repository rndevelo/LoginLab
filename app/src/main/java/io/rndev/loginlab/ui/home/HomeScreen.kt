package io.rndev.loginlab.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.rndev.loginlab.R
import io.rndev.loginlab.ui.composables.ErrorContent
import io.rndev.loginlab.ui.composables.LoadingAnimation
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Serializable
data object Home : NavKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(vm: HomeViewModel = hiltViewModel(), onSplash: () -> Unit) {

    val state = vm.uiState.collectAsState()
    val user = state.value.user
    val loggedIn = state.value.isLoggedIn
    val errorMessage = state.value.errorMessage

    LaunchedEffect(loggedIn) {
        if (loggedIn == false) {
            onSplash()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) { innerPadding ->

        when {
            errorMessage != null -> {
                ErrorContent(
                    message = errorMessage,
                    onRetry = onSplash,
                    modifier = Modifier.padding(innerPadding)
                )
            }

            loggedIn == true && user != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(bottom = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    HomeContent(
                        uid = user.uid,
                        email = user.email,
                        photoUrl = user.photoUrl,
                        displayName = user.displayName,
                        phoneNumber = user.phoneNumber,
                        creationTimestamp = user.creationTimestamp,
                        lastSignInTimestamp = user.lastSignInTimestamp,
                        modifier = Modifier.padding(innerPadding)
                    )

                    Button(
                        onClick = vm::onSignOut,
                        shape = OutlinedTextFieldDefaults.shape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = stringResource(R.string.home_text_sign_out),
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        Text(stringResource(R.string.home_text_sign_out))
                    }
                }

            }

            else -> LoadingAnimation()
        }
    }
}

@Composable
fun HomeContent(
    uid: String?,
    email: String?,
    photoUrl: String?,
    displayName: String?,
    phoneNumber: String?,
    creationTimestamp: Long?,
    lastSignInTimestamp: Long?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Cabecera del Perfil ---
        Spacer(modifier = Modifier.height(32.dp))
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photoUrl)
                .crossfade(true)
                .error(R.drawable.ic_profile)
                .build(),
            contentDescription = stringResource(R.string.home_text_user_info_photo),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = displayName ?: notData(R.string.app_text_user_info_name),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = email ?: notData(R.string.home_text_user_info_email),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- Tarjeta de Información Detallada ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                UserInfoRow(
                    icon = Icons.Default.Person,
                    label = stringResource(R.string.home_text_user_info_uid),
                    value = uid ?: notData(R.string.home_text_user_info_uid)
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                UserInfoRow(
                    icon = Icons.Default.Phone,
                    label = stringResource(R.string.home_text_user_info_phone),
                    value = phoneNumber ?: notData(R.string.home_text_user_info_phone)
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                UserInfoRow(
                    icon = Icons.Outlined.CalendarMonth,
                    label = stringResource(R.string.home_text_member_since),
                    value = formatDate(creationTimestamp)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                UserInfoRow(
                    icon = Icons.Outlined.History,
                    label = stringResource(R.string.home_text_Last_sign_in),
                    value = formatDate(lastSignInTimestamp)
                )
            }
        }
    }
}


@Composable
fun UserInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun formatDate(timestamp: Long?): String {
    val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    return if (timestamp != null) {
        sdf.format(Date(timestamp))
    } else {
        notData(R.string.home_text_user_info_creation)
    }
}

@Composable
private fun notData(homeTextUserInfo: Int): String = stringResource(
    R.string.home_text_user_info_missing,
    stringResource(homeTextUserInfo)
)