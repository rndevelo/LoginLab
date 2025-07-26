package io.rndev.loginlab.feature.core.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun PhoneOptionContent(
    isLoading: Boolean,
    title: String,
    label: String,
    initialValue: String,
    textButton: String,
    isEnabled: Boolean,
    error: String?,
    leadingIconContent: @Composable () -> Unit,
    dropDownContent: @Composable () -> Unit = {},
    onInitialValue: (String) -> Unit,
    onClick: () -> Unit,
    onBack: () -> Unit,
) {

    Column(Modifier.animateContentSize()) {
        SignInOptionTitle(
            title = title,
            onBack = onBack
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            dropDownContent()

            OutlinedTextField(
                value = initialValue,
                onValueChange = { onInitialValue(it) },
                label = { Text(label) },
                leadingIcon = leadingIconContent,
                singleLine = true,
                isError = error != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(16.dp))

        CustomButton(
            onClick = onClick,
            buttonContent = {
                if (isLoading) LoadingAnimation()
                else Text(textButton)
            },
            isEnabled = isEnabled,
        )
    }
}

