package com.example.gradetracker.ui.theme.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
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
import com.example.gradetracker.data.SchoolYear
import com.example.gradetracker.repo.GradeRepository
import com.example.gradetracker.ui.components.SchoolYearCard
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


@Composable
fun HomeScreen(navController: NavController){
    android.util.Log.d("GradeTracker", "------------------------------------------- HOMESCREEN --------------------------------------------------------------------")
    var showDialog by remember { mutableStateOf(false) }
    var schoolYearName by remember { mutableStateOf("") }

    Column(modifier = Modifier.safeDrawingPadding().fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp),) {
        Text(
            text = "GradeTracker",
            style = MaterialTheme.typography.headlineMedium
        )



        LazyColumn(modifier = Modifier.padding(8.dp)) {
            items(GradeRepository.getAllSchoolYears()) { schoolYear ->

                SchoolYearCard(
                    schoolYear = schoolYear,
                    onClick = {
                        navController.navigate("schoolYearScreen/${schoolYear.id}")
                    }
                )
            }
        }

        Button(
            onClick = {
                showDialog = true
            }
        ) {
            Text("+ Schuljahr")
        }

        if (showDialog){
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                    schoolYearName = ""
                },
                title = {Text("Schuljahr hinzufügen")},
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
                )},
                confirmButton = {
                    Button(
                        onClick = {
                            if (schoolYearName.isNotBlank()){
                                GradeRepository.addSchoolYear(SchoolYear(name = schoolYearName))
                                schoolYearName = ""
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
                            schoolYearName = ""
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