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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import com.example.gradetracker.repo.GradeRepository
import com.example.gradetracker.ui.components.SchoolYearCard
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.example.gradetracker.data.Subject
import com.example.gradetracker.ui.components.SubjectCard


@Composable
fun SchoolYearScreen(navController: NavController, schoolYearId: String?){
    android.util.Log.d("GradeTracker", "------------------------------------------- SCHOOLSCREEN --------------------------------------------------------------------")
    val schoolYear = GradeRepository.getSchoolYear(schoolYearId)
    var showDialog by remember { mutableStateOf(false) }
    var subjectName by remember { mutableStateOf("") }

    Column(modifier = Modifier.safeDrawingPadding().fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp),) {
        Text(
            text = schoolYear?.name ?:"Fehler" ,
            style = MaterialTheme.typography.headlineMedium
        )


        LazyColumn(modifier = Modifier.padding(8.dp)) {
            items(GradeRepository.getSubjectsForSchoolYear(schoolYearId)) { subject ->

                SubjectCard(
                    subject = subject,
                    onClick = {
                        navController.navigate("subjectScreen/${subject.id}")
                    }
                )
            }
        }

        Button(
            onClick = {
                showDialog = true
            }
        ) {
            Text("+ Fach")
        }

        if (showDialog){
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                    subjectName = ""
                },
                title = {Text("Fach hinzufügen")},
                text = {
                    OutlinedTextField(
                        value = subjectName,
                        onValueChange = {
                            subjectName = it
                        },
                        label = {
                            Text("Name")
                        },
                        singleLine = true
                    )},
                confirmButton = {
                    Button(
                        onClick = {
                            if (subjectName.isNotBlank()){
                                GradeRepository.addSubject(Subject(name = subjectName, schoolYearId = schoolYear?.id
                                    ?: ""))
                                subjectName = ""
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
                            subjectName = ""
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
