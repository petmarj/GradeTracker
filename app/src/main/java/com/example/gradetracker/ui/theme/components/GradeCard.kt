package com.example.gradetracker.ui.theme.components

import android.R
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import com.example.gradetracker.data.Grade

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
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.gradetracker.data.gradeColor
import com.example.gradetracker.logic.Calculator

@Composable
fun GradeCard(
    grade: Grade,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var deleteConfirmationMenu by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = {menuExpanded = true}
            )
    ) {
        Box() {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = grade.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(end = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween

                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FitnessCenter,
                            contentDescription = "Gewichtung",
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = Calculator.roundToTenth(grade.weight).toString(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,

                    ) {
                        Icon(
                            imageVector = Icons.Filled.Grade,
                            contentDescription = "Gewichtung",
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "${Calculator.roundToHundred(grade.value)}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.width(30.dp)
                        )
                    }


                }
            }
            GradeColorStripe(
                color = gradeColor(grade.value),
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
fun GradeColorStripe(color: Color, modifier: Modifier){
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