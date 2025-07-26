package io.rndev.loginlab.feature.auth.login.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DropDownMenu(
    onCodeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedCountry by remember { mutableStateOf(Country("Spain", "+34", "🇪🇸")) }

    val countries = listOf(
        Country("Spain", "+34", "🇪🇸"),
        Country("United States", "+1", "🇺🇸"),
        Country("Mexico", "+52", "🇲🇽"),
        Country("Argentina", "+54", "🇦🇷"),
        Country("Colombia", "+57", "🇨🇴"),
        Country("Chile", "+56", "🇨🇱"),
        Country("Germany", "+49", "🇩🇪"),
        Country("France", "+33", "🇫🇷"),
        Country("UK", "+44", "🇬🇧")
    )

    Row(modifier = modifier.padding(top = 8.dp)) {

        OutlinedButton(
            onClick = { expanded = true },
            shape = OutlinedTextFieldDefaults.shape,
            modifier = Modifier.fillMaxHeight()

        ) {
            Text(
                text = "${selectedCountry.flag} ${selectedCountry.code}",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(Modifier.width(8.dp))

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            countries.forEach { country ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${country.flag} ${country.name} (${country.code})",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    onClick = {
                        selectedCountry = country
                        onCodeSelected(country.code)
                        expanded = false
                    },
                )
            }
        }
    }
}

data class Country(
    val name: String,
    val code: String,
    val flag: String
)