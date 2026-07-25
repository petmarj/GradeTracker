package com.example.gradetracker.ui.theme.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TextSnippet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import com.example.gradetracker.data.Grade
import com.example.gradetracker.logic.Calculator
import com.example.gradetracker.logic.Calculator.roundToHundred
import kotlin.collections.emptyList

@Composable
fun SchoolYearScreen(navController: NavController, schoolYearId: String?) {

    val context = LocalContext.current

    val repository = remember {
        GradeRepository(DatabaseProvider.getDatabase(context))
    }

    var schoolYear by remember { mutableStateOf<SchoolYear?>(null) }
    val subjects by repository.getSubjectsForSchoolYear(schoolYearId!!).collectAsState(initial = emptyList())
    val grades by repository.getGradesForSchoolYear(schoolYearId).collectAsState(initial = emptyList())

    LaunchedEffect(schoolYearId) {

        schoolYear = repository.getSchoolYear(schoolYearId)

    }


    var showDialog by remember { mutableStateOf(false) }

    var subjectName by remember { mutableStateOf("") }
    var isEdit by remember { mutableStateOf(false) }
    var editingSubject by remember { mutableStateOf<Subject?>(null) }
    var triedToSave by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 18.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TextSnippet,
                    contentDescription = "File",
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = grades.size.toString(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Text(
                text = schoolYear?.name ?: "Fehler",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )

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
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            LazyColumn(modifier = Modifier
                .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(subjects) { subject ->

                    SubjectCard(
                        subject = subject,
                        onClick = {
                            navController.navigate("subjectScreen/${subject?.id}")
                        },
                        onEdit = {
                            showDialog = true
                            isEdit = true
                            subjectName = subject.name
                            editingSubject = subject
                        },
                        onDelete = {
                            kotlinx.coroutines.CoroutineScope(
                                kotlinx.coroutines.Dispatchers.IO
                            ).launch {
                                repository.deleteSubject(subject.id)
                            }
                        }
                    )
                }
            }

            Button(
                onClick = {
                    showDialog = true
                    triedToSave = false
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text("+ Fach")
            }

            if (showDialog) {
                val canSave = subjectName.isNotBlank()
                AlertDialog(
                    onDismissRequest = {
                        showDialog = false
                        subjectName = ""
                        editingSubject = null
                        isEdit = false
                    },
                    title = {
                        if (isEdit) {
                            Text("Fach bearbeiten")
                        } else {
                            Text("Neues Fach")
                        }
                    },
                    text = {
                        OutlinedTextField(
                            value = subjectName,
                            onValueChange = {
                                subjectName = it
                            },
                            label = {
                                Text("Name")
                            },
                            singleLine = true,
                            isError = triedToSave && !subjectName.isNotBlank(),
                            supportingText = {
                                if (triedToSave && !subjectName.isNotBlank()) {
                                    Text("Bitte Namen eingeben")
                                }
                            }
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                triedToSave = true
                                if (canSave) {
                                    if (isEdit) {
                                        editingSubject?.let { oldGrade ->
                                            val updatedSubject = oldGrade.copy(
                                                name = subjectName
                                            )

                                            kotlinx.coroutines.CoroutineScope(
                                                kotlinx.coroutines.Dispatchers.IO
                                            ).launch {
                                                repository.updateSubject(updatedSubject)
                                            }
                                        }
                                    } else {
                                        val newSubject = Subject(
                                            name = subjectName,
                                            schoolYearId = schoolYearId
                                        )
                                        kotlinx.coroutines.CoroutineScope(
                                            kotlinx.coroutines.Dispatchers.IO
                                        ).launch {
                                            repository.addSubject(newSubject)
                                        }
                                    }
                                    subjectName = ""
                                    showDialog = false
                                    isEdit = false
                                    editingSubject = null
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
                                isEdit = false
                                editingSubject = null
                            }
                        ) {
                            Text("Abbrechen")
                        }
                    }
                )
            }
        }
    }
}
