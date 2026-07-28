package com.example.gradetracker.ui.home

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gradetracker.model.SchoolYear
import com.example.gradetracker.model.GradeColorMode
import com.example.gradetracker.data.local.database.DatabaseProvider
import com.example.gradetracker.model.schoolYearColor
import com.example.gradetracker.domain.Calculator
import com.example.gradetracker.domain.Calculator.roundToHundred
import com.example.gradetracker.data.repository.GradeRepository
import kotlin.math.abs

@Composable
fun SchoolYearCard(
    schoolYear: SchoolYear,
    gradeColorMode: GradeColorMode,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val context = LocalContext.current

    val repository = remember {
        GradeRepository(DatabaseProvider.getDatabase(context))
    }
    val grades by repository.getGradesForSchoolYear(schoolYear.id).collectAsState(initial = emptyList())
    val subjects by repository.getSubjectsForSchoolYear(schoolYear.id).collectAsState(initial = emptyList())
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
                    text = schoolYear.name,
                    style = MaterialTheme.typography.titleLarge
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(end = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.FormatListBulleted,
                            contentDescription = "List",
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            style = MaterialTheme.typography.bodyMedium,
                            text = "${subjects.size}"
                        )
                    }


                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (Calculator.getPointsForSchoolYear(grades = grades, subjects = subjects) == null || Calculator.getPointsForSchoolYear(grades = grades, subjects = subjects)!! >= 0) {
                                    Icons.Filled.Add
                                }else{
                                    Icons.Filled.Remove
                                },
                                contentDescription = "Gewichtung",
                                modifier = Modifier.size(18.dp)
                            )


                            val points = Calculator.getPointsForSchoolYear(
                                grades = grades,
                                subjects = subjects
                            )
                            val pointsText = points
                                ?.let { Calculator.roundToTenth(it) }
                                ?.let { abs(it) }
                                ?.toString()
                                ?: "-.--"
                            Text(
                                text = pointsText,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.width(40.dp)
                            )
                        }
                        Text(
                            text = if (Calculator.getAverageForSchoolYear(
                                    grades = grades,
                                    subjects = subjects
                                ) == null
                            ) {
                                "Ø -.--"
                            } else {
                                "Ø ${
                                    roundToHundred(
                                        Calculator.getAverageForSchoolYear(
                                            grades = grades,
                                            subjects = subjects
                                        )
                                    )
                                }"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.width(50.dp)
                        )

                    }


                }
            }
            ColorStripe(
                color = schoolYearColor(
                    subjects = subjects,
                    grades = grades,
                    mode = gradeColorMode
                ),
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


@Composable
fun ColorStripe(color: Color, modifier: Modifier){
    Canvas(modifier = modifier) {
        val stripeWidth = 20.dp.toPx()

        drawRect(
            color = color,
            size = Size(
                width = stripeWidth,
                height = size.height
            ),
            topLeft = Offset(x = size.width - stripeWidth, y = 0f)
        )
    }
}
