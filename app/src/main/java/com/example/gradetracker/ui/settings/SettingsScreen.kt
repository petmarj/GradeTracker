package com.example.gradetracker.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gradetracker.ui.theme.ThemeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    var showTokenMenu by remember{mutableStateOf(false) }
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
                onTestConnection = viewModel::testConnection,
                onAddToken = {showTokenMenu = true},
                onDeleteToken = {
                    viewModel.deleteToken()
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

    if(showTokenMenu){
        AlertDialog(
            onDismissRequest = {
                showTokenMenu = false
            },
            title = {Text("Token speichern")},
            text = {
                OutlinedTextField(
                    value = tokenString,
                    label = {
                        Text("Token")
                    },
                    onValueChange = {
                        tokenString = it
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = if (tokenString.isNotBlank()){
                        {
                            viewModel.storeToken(tokenString)
                            showTokenMenu = false
                            tokenString = ""
                        }
                    } else {
                        {}
                    }
                )
                {
                    Text("Speichern")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showTokenMenu = false
                        tokenString = ""
                    }
                ) {
                    Text("Abbrechen")
                }
            }

        )
    }
}