package com.example.gradetracker.ui.theme.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.collectAsState
import com.example.gradetracker.data.SchoolYear
import com.example.gradetracker.repo.GradeRepository
import com.example.gradetracker.ui.components.SchoolYearCard
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.gradetracker.data.Subject
import com.example.gradetracker.data.database.DatabaseProvider
import com.example.gradetracker.logic.Calculator
import kotlinx.coroutines.launch
import java.nio.file.WatchEvent


@Composable
fun HomeScreen(navController: NavController){
    android.util.Log.d("GradeTracker", "------------------------------------------- HOMESCREEN --------------------------------------------------------------------")
    var showDialog by remember { mutableStateOf(false) }
    var schoolYearName by remember { mutableStateOf("") }
    var isEdit by remember { mutableStateOf(false) }
    var editingSchoolYear by remember { mutableStateOf<SchoolYear?>(null) }
    var triedToSave by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val repository = remember {
        GradeRepository(
            DatabaseProvider.getDatabase(context)
        )
    }
    val schoolYears by repository.getAllSchoolYears()
        .collectAsState(initial = emptyList())



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),

    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            Text(
                text = "Noten",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
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
                items(schoolYears) { schoolYear ->

                    SchoolYearCard(
                        schoolYear = schoolYear,
                        onClick = {
                            navController.navigate("schoolYearScreen/${schoolYear.id}")
                        },
                        onEdit = {
                            showDialog = true
                            isEdit = true
                            schoolYearName = schoolYear.name
                            editingSchoolYear = schoolYear
                        },
                        onDelete = {
                            kotlinx.coroutines.CoroutineScope(
                                kotlinx.coroutines.Dispatchers.IO
                            ).launch {
                                repository.deleteSchoolYear(schoolYear.id)
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
                Text("+ Schuljahr")
            }

            if (showDialog) {
                val canSave = schoolYearName.isNotBlank()
                AlertDialog(
                    onDismissRequest = {
                        showDialog = false
                        schoolYearName = ""
                        isEdit = false
                        editingSchoolYear = null
                    },
                    title = { if(isEdit){
                        Text("Schuljahr bearbeiter")
                    } else {
                        Text("Neues Schuljahr")
                    }
                    },
                    text = {
                        OutlinedTextField(
                            value = schoolYearName,
                            onValueChange = {
                                schoolYearName = it
                            },
                            label = {
                                Text("Name")
                            },
                            singleLine = true
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                triedToSave = true
                                if (canSave) {
                                    if (isEdit) {
                                        editingSchoolYear?.let { oldSchoolyear ->
                                            val updatedSchoolYear = oldSchoolyear.copy(
                                                name = schoolYearName
                                            )

                                            kotlinx.coroutines.CoroutineScope(
                                                kotlinx.coroutines.Dispatchers.IO
                                            ).launch {
                                                repository.updateSchoolYear(updatedSchoolYear)
                                            }
                                        }
                                    } else {
                                        val newYear = SchoolYear(name = schoolYearName)
                                        kotlinx.coroutines.CoroutineScope(
                                            kotlinx.coroutines.Dispatchers.IO
                                        ).launch {
                                            repository.addSchoolYear(newYear)
                                        }
                                    }
                                    schoolYearName = ""
                                    showDialog = false
                                    isEdit = false
                                    editingSchoolYear = null
                                }
                            },


                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                schoolYearName = ""
                                showDialog = false
                                isEdit = false
                                editingSchoolYear = null
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
