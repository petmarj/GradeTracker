package com.example.gradetracker.ui.components

import android.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TextSnippet
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gradetracker.data.Grade
import com.example.gradetracker.data.Subject
import com.example.gradetracker.data.database.DatabaseProvider
import com.example.gradetracker.data.gradeColor
import com.example.gradetracker.logic.Calculator
import com.example.gradetracker.logic.Calculator.roundToHundred
import com.example.gradetracker.repo.GradeRepository
import com.example.gradetracker.ui.theme.components.GradeColorStripe

@Composable
fun SubjectCard(
    subject: Subject,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {

    val context = LocalContext.current

    val repository = remember {
        GradeRepository(DatabaseProvider.getDatabase(context))
    }

    val grades by repository.getGradesForSubject(subject.id)
        .collectAsState(initial = emptyList())

    var menuExpanded by remember { mutableStateOf(false) }
    var deleteConfirmationMenu by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = {menuExpanded = true}
            )
    ) {
        Box() {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.titleLarge
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(end=10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TextSnippet,
                            contentDescription = "File",
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            style = MaterialTheme.typography.bodyMedium,
                            text = "${grades.size}"
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,

                        ) {
                        Text(
                            style = MaterialTheme.typography.bodyMedium,
                            text = "Ø"
                            )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if(Calculator.getAverageForGrades(grades) == null){"-.--"}else{roundToHundred(Calculator.getAverageForGrades(grades)).toString()},
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.width(30.dp))
                    }
                }

            }
            GradeColorStripe(
                color = gradeColor(Calculator.getAverageForGrades(grades)),
                modifier = Modifier.matchParentSize()
            )
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                modifier = Modifier.padding(5.dp)
            ) {
                Column(verticalArrangement = Arrangement.SpaceEvenly) {
                    TextButton(
                        onClick = {
                            onEdit()
                            menuExpanded = false
                        },
                        modifier = Modifier.fillMaxWidth()

                    ) {
                        Text("Bearbeiten")
                    }
                    TextButton(
                        onClick = {
                            deleteConfirmationMenu = true
                            menuExpanded = false
                        },
                        modifier = Modifier.fillMaxWidth()

                    ) {
                        Text("Löschen")
                    }
                }
            }
            if (deleteConfirmationMenu) {
                AlertDialog(
                    onDismissRequest = {
                        deleteConfirmationMenu = false
                    },
                    title = {
                        Text(
                            text = "Bist du dir sicher?",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    text = {
                        Text(
                            text = "Diese Aktion kann nicht rückgängig gemacht werden.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    confirmButton = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = {
                                    deleteConfirmationMenu = false
                                }
                            ) {
                                Text("Abbrechen")
                            }

                            OutlinedButton(
                                onClick = {
                                    deleteConfirmationMenu = false
                                    onDelete()
                                }
                            ) {
                                Text("Löschen")
                            }
                        }

                    },
                    dismissButton = null
                )
            }
        }
    }
}