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
import androidx.compose.runtime.LaunchedEffect
import com.example.gradetracker.repo.GradeRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.gradetracker.data.SchoolYear
import com.example.gradetracker.data.Subject
import com.example.gradetracker.data.database.DatabaseProvider
import com.example.gradetracker.ui.components.SubjectCard
import kotlinx.coroutines.launch


@Composable
fun SchoolYearScreen(navController: NavController, schoolYearId: String?) {

    val context = LocalContext.current

    val repository = remember {
        GradeRepository(DatabaseProvider.getDatabase(context))
    }

    var schoolYear by remember { mutableStateOf<SchoolYear?>(null) }
    var subjects by remember { mutableStateOf<List<Subject?>>(emptyList()) }

    LaunchedEffect(schoolYearId) {
        if (schoolYearId != null) {
            schoolYear = repository.getSchoolYear(schoolYearId)
            subjects = repository.getSubjectsForSchoolYear(schoolYearId)
        }

    }

    var showDialog by remember { mutableStateOf(false) }
    var subjectName by remember { mutableStateOf("") }


    Column(modifier = Modifier.safeDrawingPadding().fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp),) {
        Text(
            text = schoolYear?.name ?:"Fehler" ,
            style = MaterialTheme.typography.headlineMedium
        )


        LazyColumn(modifier = Modifier.padding(8.dp)) {
            items(subjects) { subject ->

                SubjectCard(
                    subject = subject,
                    onClick = {
                        navController.navigate("subjectScreen/${subject?.id}")
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
                                val newSubject = Subject(name = subjectName, schoolYearId = schoolYearId)


                                kotlinx.coroutines.CoroutineScope(
                                    kotlinx.coroutines.Dispatchers.IO
                                ).launch {
                                    repository.addSubject(newSubject)
                                }
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
