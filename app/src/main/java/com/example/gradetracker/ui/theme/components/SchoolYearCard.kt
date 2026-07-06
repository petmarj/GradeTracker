package com.example.gradetracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsActions.OnClick
import androidx.compose.ui.unit.dp
import com.example.gradetracker.data.Grade
import com.example.gradetracker.data.SchoolYear
import com.example.gradetracker.data.database.DatabaseProvider
import com.example.gradetracker.logic.Calculator
import com.example.gradetracker.repo.GradeRepository
import com.example.gradetracker.ui.theme.screens.SchoolYearScreen

@Composable
fun SchoolYearCard(
    schoolYear: SchoolYear,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    val repository = remember {
        GradeRepository(DatabaseProvider.getDatabase(context))
    }
    var grades by remember { mutableStateOf<List<Grade>>(emptyList()) }
    LaunchedEffect(schoolYear) {
        grades = repository.getGradesForSchoolYear(schoolYearId = schoolYear.id)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {onClick()}
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = schoolYear.name,
                style = MaterialTheme.typography.titleLarge
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "%.2f  (${Calculator.getPointsForGrades(grades)})".format(
                        Calculator.getAverageForGrades(grades)
                    )
                )

                Text(
                    "${grades.size} Fächer"
                )
            }
        }
    }
}