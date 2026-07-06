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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.gradetracker.data.Subject
import com.example.gradetracker.data.database.DatabaseProvider
import com.example.gradetracker.logic.Calculator
import com.example.gradetracker.repo.GradeRepository

@Composable
fun SubjectCard(
    subject: Subject?,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    val repository = remember {
        GradeRepository(DatabaseProvider.getDatabase(context))
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
                text = subject?.name ?: "Fehler",
                style = MaterialTheme.typography.titleLarge
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Schnitt: %.2f".format(
                        Calculator.getAverageForGrades(repository.getGradesForSubject(subject?.id))
                    )
                )

                Text(
                    "${repository.getGradesForSubject(subjectId = subject?.id).size} Tests"
                )
            }
        }
    }
}