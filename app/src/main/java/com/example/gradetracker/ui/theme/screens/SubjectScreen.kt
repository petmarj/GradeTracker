package com.example.gradetracker.ui.theme.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.example.gradetracker.repo.GradeRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import com.example.gradetracker.data.Grade
import com.example.gradetracker.data.Subject
import com.example.gradetracker.data.database.DatabaseProvider
import com.example.gradetracker.ui.theme.components.GradeCard
import kotlinx.coroutines.launch


@Composable
fun SubjectScreen(
    navController: NavController,
    subjectId: String?,
){
    val context = LocalContext.current

    val repository = remember {
        GradeRepository(DatabaseProvider.getDatabase(context))
    }
    android.util.Log.d("GradeTracker", "------------------------------------------- SCHOOLSCREEN --------------------------------------------------------------------")
    var showDialog by remember { mutableStateOf(false) }
    var gradeName by remember { mutableStateOf("") }
    var gradeValue by remember { mutableStateOf("") }
    var gradeWeight by remember { mutableStateOf("1.0") }
    var subject by remember { mutableStateOf<Subject?>(null) }
    val grades by repository.getGradesForSubject(subject?.id).collectAsState(initial = emptyList())
    LaunchedEffect(subjectId) {
        if (subjectId!=null){
            subject = repository.getSubject(subjectId)
        }
    }


    Column(modifier = Modifier
        .safeDrawingPadding()
        .fillMaxSize()
        .padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp),) {
        Text(
            text = subject?.name ?:"Fehler" ,
            style = MaterialTheme.typography.headlineMedium
        )


        LazyColumn(modifier = Modifier.padding(8.dp)) {
            items(grades) { grade ->

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
                                val newGrade = Grade(name = gradeName, weight = gradeWeight.toDouble(), value = gradeValue.toDouble(), subjectId = subjectId)


                                kotlinx.coroutines.CoroutineScope(
                                    kotlinx.coroutines.Dispatchers.IO
                                ).launch {
                                    repository.addGrade(newGrade)
                                }
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
