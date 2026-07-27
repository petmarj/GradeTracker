package com.example.gradetracker.ui.settings

import android.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.gradetracker.model.GradeSort
import com.example.gradetracker.model.SubjectSort
import com.example.gradetracker.ui.theme.ThemeMode

@Composable
fun PreferencesCard(
    state: SettingsUiState,
    onGradeSet: (GradeSort) -> Unit,
    onSubjectSet: (SubjectSort) -> Unit,
) {
    val gradeSortText = when (state.gradeSort) {
        GradeSort.NEWEST -> "Neuste"
        GradeSort.OLDEST -> "Älteste"
        GradeSort.VALUE_DESC -> "Beste Note"
        GradeSort.NAME -> "Name"
    }


    val subjectSortText = when (state.subjectSort) {
        SubjectSort.NEWEST -> "Neuste"
        SubjectSort.OLDEST -> "Älteste"
        SubjectSort.VALUE_DESC -> "Bester Schnitt"
        SubjectSort.NAME -> "Name"
    }

    var showGradeSortDialog by remember {
        mutableStateOf(false)
    }

    var showSubjectSortDialog by remember {
        mutableStateOf(false)
    }
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Präferenzen",
                style = MaterialTheme.typography.titleLarge
            )
            HorizontalDivider(thickness = 2.dp)
            Column() {
                Text(
                    text = "Sortierung",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                PreferenceRow(
                    title = "Noten",
                    selectedValue = gradeSortText,
                    onClick = {
                        showGradeSortDialog = true
                    }
                )
                PreferenceRow(
                    title = "Fächer",
                    selectedValue = subjectSortText,
                    onClick = {
                        showSubjectSortDialog = true
                    }
                )
            }

        }
    }
    if (showGradeSortDialog) {
        Dialog(
            onDismissRequest = {
                showGradeSortDialog = false
            }
        ) {
            Surface(
                modifier = Modifier.widthIn(
                    min = 260.dp,
                    max = 300.dp
                ),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = 32.dp,
                        vertical = 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Noten sortieren",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(Modifier.height(4.dp))
                    HorizontalDivider(thickness = 2.dp)
                    Spacer(Modifier.height(8.dp))

                    GradeSort.entries.forEach { sort ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onGradeSet(sort)
                                    showGradeSortDialog = false
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = state.gradeSort == sort,
                                onClick = null,
                                modifier = Modifier.size(36.dp)
                            )

                            Spacer(Modifier.width(4.dp))

                            Text(
                                text = when (sort) {
                                    GradeSort.NEWEST -> "Neueste"
                                    GradeSort.OLDEST -> "Älteste"
                                    GradeSort.VALUE_DESC -> "Beste Note"
                                    GradeSort.NAME -> "Name"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    if (showSubjectSortDialog) {
        Dialog(
            onDismissRequest = {
                showSubjectSortDialog = false
            }
        ) {
            Surface(
                modifier = Modifier.widthIn(
                    min = 260.dp,
                    max = 300.dp
                ),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = 32.dp,
                        vertical = 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Fächer sortieren",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.height(4.dp))
                    HorizontalDivider(thickness = 2.dp)
                    Spacer(Modifier.height(8.dp))

                    SubjectSort.entries.forEach { sort ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSubjectSet(sort)
                                    showSubjectSortDialog = false
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = state.subjectSort == sort,
                                onClick = null,
                                modifier = Modifier.size(36.dp)
                            )

                            Spacer(Modifier.width(4.dp))

                            Text(
                                text = when (sort) {
                                    SubjectSort.NEWEST -> "Neueste"
                                    SubjectSort.OLDEST -> "Älteste"
                                    SubjectSort.VALUE_DESC -> "Beste Note"
                                    SubjectSort.NAME -> "Name"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PreferenceRow(
    title: String,
    selectedValue: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = selectedValue,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}