package com.example.gradetracker.ui.theme.screens

import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.rememberDatePickerState
import com.example.gradetracker.repo.GradeRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.navigation.NavController
import com.example.gradetracker.data.Grade
import com.example.gradetracker.ui.theme.components.GradeCard
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId


@Composable
fun SubjectScreen(
    navController: NavController,
    subjectId: String?,
){
    android.util.Log.d("GradeTracker", "------------------------------------------- SCHOOLSCREEN --------------------------------------------------------------------")
    val subject = GradeRepository.getSubject(subjectId)
    var editGrade: Grade
    var showDialog by remember { mutableStateOf(false) }
    var showDialogEdit by remember { mutableStateOf(false) }
    var gradeName by remember { mutableStateOf("") }
    var gradeValue by remember { mutableStateOf("") }
    var gradeWeight by remember { mutableStateOf("1.0") }


    Column(modifier = Modifier.safeDrawingPadding().fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp),) {
        Text(
            text = subject?.name ?:"Fehler" ,
            style = MaterialTheme.typography.headlineMedium
        )


        LazyColumn(modifier = Modifier.padding(8.dp)) {
            items(GradeRepository.getGradesForSubject(subjectId)) { grade ->

                GradeCard(
                    grade = grade,
                    onClick = {

                    }
                )
            }
        }

        Button(
            onClick = {
                showDialog = true
            }
        ) {
            Text("+ Note")
        }

        if (showDialog){
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                    gradeName = ""
                    gradeValue = ""
                    gradeWeight = "1.0"
                },
                title = {Text("Note hinzufügen")},
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = gradeName,
                            onValueChange = {
                                gradeName = it
                            },
                            label = {
                                Text("Name")
                            },
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = gradeValue,
                            onValueChange = {
                                gradeValue = it
                            },
                            label = {
                                Text("Note")
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )

                        OutlinedTextField(
                            value = gradeWeight,
                            onValueChange = {
                                gradeWeight = it
                            },
                            label = {
                                Text("Gewichtung")
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )

                        }

                    },
                confirmButton = {
                    Button(
                        onClick = {
                            if (gradeName.isNotBlank() && gradeValue.isNotBlank() && gradeWeight.isNotBlank()){
                                GradeRepository.addGrade(Grade(name = gradeName, value = gradeValue.toDouble(), weight = gradeWeight.toDouble(), subjectId = subjectId))
                                gradeName = ""
                                gradeWeight = "1.0"
                                gradeValue = ""
                                showDialog = false
                            }


                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            gradeName = ""
                            gradeValue = ""
                            gradeWeight = "1.0"
                            showDialog = false
                        }
                    ) {
                        Text("Abbrechen")
                    }
                }
            )
        }

    }
}
