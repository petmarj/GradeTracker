package com.example.gradetracker.ui.subject

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.example.gradetracker.data.repository.GradeRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.gradetracker.model.Grade
import com.example.gradetracker.model.GradeSort
import com.example.gradetracker.model.SchoolYear
import com.example.gradetracker.model.Subject
import com.example.gradetracker.data.local.database.DatabaseProvider
import com.example.gradetracker.domain.Calculator
import com.example.gradetracker.domain.Calculator.roundToHundred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun SubjectScreen(
    navController: NavController,
    subjectId: String?,
    defaultGradeSort: GradeSort
){
    val context = LocalContext.current

    val repository = remember {
        GradeRepository(DatabaseProvider.getDatabase(context))
    }
    Log.d("GradeTracker", "------------------------------------------- SCHOOLSCREEN --------------------------------------------------------------------")
    var showDialog by remember { mutableStateOf(false) }
    var showWishGrade by remember { mutableStateOf(false) }
    var isEdit by remember { mutableStateOf(false) }
    var editingGrade by remember { mutableStateOf<Grade?>(null) }
    var gradeName by remember { mutableStateOf("") }
    var gradeValue by remember { mutableStateOf("") }
    var wishGradeValue by remember { mutableStateOf("") }
    var wishGradeWeight by remember { mutableStateOf("1.0") }
    var neededGrade: Double? by remember { mutableStateOf(null) }
    var gradeWeight by remember { mutableStateOf("1.0") }
    var subject by remember { mutableStateOf<Subject?>(null) }
    val grades by repository.getGradesForSubject(subject?.id).collectAsState(initial = emptyList())
    var triedToSave by remember { mutableStateOf(false) }
    var triedToCalculate by remember { mutableStateOf(false) }
    var schoolYear by remember { mutableStateOf<SchoolYear?>(null) }
    var gradeSort by remember { mutableStateOf(defaultGradeSort) }
    var filterMenuExpanded by remember { mutableStateOf(false) }
    val selectedColor = Color.Blue
    val unselectedColor = Color.White

    LaunchedEffect(subjectId) {
        if (subjectId!=null){
            subject = repository.getSubject(subjectId)
        }
        schoolYear = repository.getSchoolYear(subject?.schoolYearId)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = schoolYear?.name ?: "Fehler",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 10.dp)
            ) {
                Text(
                    text = subject?.name ?: "Fehler",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                )
                IconButton(
                    onClick = {
                        filterMenuExpanded = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Sort,
                        contentDescription = "Sortierung",
                    )
                    DropdownMenu(
                        expanded = filterMenuExpanded,
                        onDismissRequest = { filterMenuExpanded = false },
                        modifier = Modifier.padding(5.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.SpaceEvenly) {
                            TextButton(
                                onClick = {
                                    gradeSort = GradeSort.NEWEST
                                    filterMenuExpanded = false
                                },
                                modifier = Modifier.fillMaxWidth()

                            ) {
                                Text("Neueste", color = if(gradeSort == GradeSort.NEWEST) {selectedColor} else {unselectedColor})
                            }
                            TextButton(
                                onClick = {
                                    gradeSort = GradeSort.OLDEST
                                    filterMenuExpanded = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.textButtonColors()

                            ) {
                                Text("Älteste", color = if(gradeSort == GradeSort.OLDEST) {selectedColor} else {unselectedColor})
                            }
                            TextButton(
                                onClick = {
                                    gradeSort = GradeSort.NAME
                                    filterMenuExpanded = false
                                },
                                modifier = Modifier.fillMaxWidth()

                            ) {
                                Text("Name", color = if(gradeSort == GradeSort.NAME) {selectedColor} else {unselectedColor})
                            }
                            TextButton(
                                onClick = {
                                    gradeSort = GradeSort.VALUE_DESC
                                    filterMenuExpanded = false
                                },
                                modifier = Modifier.fillMaxWidth()

                            ) {
                                Text("Beste Note", color = if(gradeSort == GradeSort.VALUE_DESC) {selectedColor} else {unselectedColor})
                            }
                        }
                    }
                }


            }

            Text(
                text = "Ø " + roundToHundred(Calculator.getAverageForGrades(grades)).toString(),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
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
                val sortedGrades = when (gradeSort){
                    GradeSort.NEWEST -> grades.sortedByDescending { it.timeCreated }
                    GradeSort.OLDEST -> grades.sortedBy { it.timeCreated }
                    GradeSort.NAME -> grades.sortedBy { it.name }
                    GradeSort.VALUE_DESC -> grades.sortedByDescending { it.value }
                }
                items(sortedGrades) { grade ->

                    GradeCard(
                        grade = grade,
                        onEdit = {
                            showDialog = true
                            isEdit = true
                            gradeValue = grade.value.toString()
                            gradeWeight = grade.weight.toString()
                            gradeName = grade.name
                            editingGrade = grade

                        },
                        onDelete = {
                            CoroutineScope(
                                Dispatchers.IO
                            ).launch {
                                repository.deleteGrade(grade.id)
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
                Text("+ Note")
            }
            Button(
                onClick = {
                    showWishGrade = true
                    triedToCalculate = false
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Text("Wunschnote")
            }

            if (showDialog) {

                val parsedGrade = gradeValue.toDoubleOrNull()
                val parsedWeight = gradeWeight.toDoubleOrNull()

                val gradeIsValid = parsedGrade != null && parsedGrade in 1.0..6.0
                val weightIsValid = parsedWeight != null && parsedWeight >= 0.0
                val canSave = gradeName.isNotBlank() && gradeIsValid && weightIsValid
                AlertDialog(
                    onDismissRequest = {
                        showDialog = false
                        isEdit = false
                        gradeName = ""
                        gradeValue = ""
                        gradeWeight = "1.0"
                        editingGrade = null
                    },
                    title = { if(isEdit){
                        Text("Note bearbeiten")
                    }else{
                        Text("Note hinzufügen")
                    } },
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
                                singleLine = true,
                                isError = triedToSave && !gradeName.isNotBlank(),
                                supportingText = {
                                    if (triedToSave && !gradeName.isNotBlank()) {
                                        Text("Bitte Namen eingeben")
                                    }
                                }
                            )

                            OutlinedTextField(
                                value = gradeValue.replace(",", "."),
                                onValueChange = {
                                    gradeValue = it
                                },
                                label = {
                                    Text("Note")
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),

                                isError = triedToSave && !gradeIsValid,
                                supportingText = {
                                    if (triedToSave && !gradeIsValid) {
                                        Text("Note ungültig")
                                    }
                                }
                            )

                            OutlinedTextField(
                                value = gradeWeight.replace(",", "."),
                                onValueChange = {
                                    gradeWeight = it
                                },
                                label = {
                                    Text("Gewichtung")
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                isError = triedToSave && !weightIsValid,
                                supportingText = {
                                    if (triedToSave && !weightIsValid) {
                                        Text("Gewicht ungültig")
                                    }
                                }
                            )

                        }

                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                triedToSave = true
                                if (canSave) {
                                    if (isEdit){
                                        editingGrade?.let { oldGrade ->
                                            val updatedGrade = oldGrade.copy(
                                                name = gradeName,
                                                value = parsedGrade,
                                                weight = parsedWeight,
                                                subjectId = subjectId
                                            )

                                            CoroutineScope(
                                                Dispatchers.IO
                                            ).launch {
                                                repository.updateGrade(updatedGrade)
                                            }
                                        }

                                    } else {
                                        val newGrade = Grade(
                                            name = gradeName,
                                            weight = gradeWeight.toDouble(),
                                            value = gradeValue.toDouble(),
                                            subjectId = subjectId
                                        )
                                        CoroutineScope(
                                            Dispatchers.IO
                                        ).launch {
                                            repository.addGrade(newGrade)
                                        }
                                    }

                                    gradeName = ""
                                    gradeWeight = "1.0"
                                    gradeValue = ""
                                    showDialog = false
                                    triedToSave = false
                                    isEdit = false
                                    editingGrade = null
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
                                isEdit = false
                                editingGrade = null
                            }
                        ) {
                            Text("Abbrechen")
                        }
                    }
                )
            }

            if (showWishGrade){
                val parsedGrade = wishGradeValue.toDoubleOrNull()
                val parsedWeight = wishGradeWeight.toDoubleOrNull()

                val gradeIsValid = parsedGrade != null && parsedGrade in 1.0..6.0
                val weightIsValid = parsedWeight != null && parsedWeight > 0.0
                AlertDialog(
                    onDismissRequest = {
                        showWishGrade = false
                        wishGradeValue = ""
                        wishGradeWeight = "1.0"
                        triedToCalculate = false
                        neededGrade = null
                    },
                    title = {
                        Text("Wunschnotenrechner")
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = wishGradeValue,
                                onValueChange = {
                                     wishGradeValue = it
                                },
                                label = {
                                    Text("Wunschnote")
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                isError = triedToCalculate && wishGradeValue.isBlank(),
                                supportingText = {
                                    if (triedToCalculate && wishGradeValue.isBlank()) {
                                        Text("Bitte Wunschnote eingeben")
                                    }
                                }
                            )
                            OutlinedTextField(
                                value = wishGradeWeight,
                                onValueChange = {
                                    wishGradeWeight = it
                                },
                                label = {
                                    Text("Benötigte Note Gewicht")
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                isError = triedToCalculate && !weightIsValid,
                                supportingText = {
                                    if (triedToCalculate && !weightIsValid) {
                                        Text("Gewicht ungültig")
                                    }
                                }
                            )
                            OutlinedTextField(
                                readOnly = true,
                                label = {
                                    Text(
                                        text = "Benötigt",
                                        maxLines = 1
                                    )
                                },
                                value =  if(neededGrade == null) {
                                    ""
                                } else {
                                    roundToHundred(neededGrade).toString()
                                } ,
                                onValueChange = {},
                                isError = neededGrade?.let { it !in 1.0..6.0 } ?: false,
                                supportingText = {

                                    /*if (neededGrade?.let { it !in 1.0..6.0 } == true) {
                                        Text(
                                            text = "Nicht erreichbar",
                                            maxLines = 1
                                        )
                                    }*/

                                },
                                modifier = Modifier.width(130.dp).fillMaxWidth().align(Alignment.End)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween

                            ) {
                                TextButton(
                                    onClick = {
                                        showWishGrade = false
                                        wishGradeValue = ""
                                        wishGradeWeight = "1.0"
                                        triedToCalculate = false
                                        neededGrade = null
                                    }
                                ) {
                                    Text("Schliessen")
                                }
                                Button(
                                    onClick = {
                                        triedToCalculate = true
                                        if (gradeIsValid && weightIsValid) {
                                            neededGrade = Calculator.neededGradeForGoal(
                                                grades = grades,
                                                goal = wishGradeValue.toDouble(),
                                                weight = wishGradeWeight.toDouble()
                                            )

                                        }
                                    }
                                ) {
                                    Text("Ausrechnen")
                                }

                            }
                        }
                    },
                    confirmButton = {

                    }
                )
            }

        }

    }
}
