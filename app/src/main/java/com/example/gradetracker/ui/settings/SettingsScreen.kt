package com.example.gradetracker.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gradetracker.ui.theme.ThemeMode

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    var showLoginMenu by remember{mutableStateOf(false) }
    var tokenString by remember{mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Einstellungen",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }


        item {
            ApiConnectionCard(
                state = state,
                onTestConnection = {},
                onLogin = {showLoginMenu = true},
                onLogout = {
                    viewModel.logout()
                }
            )
        }

        item {
            DesignCard(
                state = state,
                onThemeModeChange = onThemeModeChange,
                themeMode = themeMode,
            )
        }


        item {
            PreferencesCard(
                state = state,
                onGradeSet = {
                    viewModel.setGradeSort(it)
                },
                onSubjectSet = {
                    viewModel.setSubjectSort(it)
                },
                onGradeColorModeSet = {
                    viewModel.setGradeColorMode(it)
                }
            )
        }

        item {
            Text("Export und Import")
        }
        item {
            HorizontalDivider(thickness = 2.dp)
        }
        item {
            Text("Über")
        }
        item {
            HorizontalDivider(thickness = 2.dp)
        }
        item {
            Text("GradeTracker 1.0")
        }
    }

    if(showLoginMenu){
        LoginBottomSheet(
            state = state,
            onDismiss = {
                showLoginMenu = false
            },
            onLogin = viewModel::login
        )
    }
}
