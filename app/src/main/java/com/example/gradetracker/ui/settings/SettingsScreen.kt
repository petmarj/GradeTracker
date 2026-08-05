package com.example.gradetracker.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    var showLoginMenu by remember { mutableStateOf(false) }
    var tokenString by remember { mutableStateOf("") }
    val plusPointsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    viewModel.importPlusPoints(inputStream)
                } else {
                    viewModel.reportImportError("Die ausgewählte Datei konnte nicht geöffnet werden.")
                }
            } catch (exception: Exception) {
                viewModel.reportImportError(
                    exception.message ?: "Die ausgewählte Datei konnte nicht geöffnet werden."
                )
            }
        }
    }

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
                onLogin = { showLoginMenu = true },
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
            PlusPointsImportCard(
                importState = state.plusPointsImportState,
                onChooseFile = {
                    plusPointsLauncher.launch(arrayOf("*/*"))
                }
            )
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

    if (showLoginMenu) {
        LoginBottomSheet(
            state = state,
            onDismiss = {
                showLoginMenu = false
            },
            onLogin = viewModel::login
        )
    }
}

@Composable
private fun PlusPointsImportCard(
    importState: PlusPointsImportState,
    onChooseFile: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "PlusPoints importieren",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Importiert ein PlusPoints-Semester mit Fächern und Tests."
            )
            Button(
                onClick = onChooseFile,
                enabled = importState != PlusPointsImportState.Importing
            ) {
                if (importState == PlusPointsImportState.Importing) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.UploadFile,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                Text(
                    if (importState == PlusPointsImportState.Importing) {
                        "Wird importiert …"
                    } else {
                        "Datei auswählen"
                    }
                )
            }

            when (importState) {
                PlusPointsImportState.Idle,
                PlusPointsImportState.Importing -> Unit

                is PlusPointsImportState.Success -> Text(
                    text = "„${importState.semesterName}“ importiert: " +
                            "${importState.subjectCount} Fächer, ${importState.gradeCount} Prüfungen.",
                    color = MaterialTheme.colorScheme.primary
                )

                is PlusPointsImportState.Failed -> Text(
                    text = importState.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
