package com.example.gradetracker.ui.student

import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CoursesCard(
    state: StudentUiState
) {
    val student = state.student
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(8.dp),

    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Fächer",
                style = MaterialTheme.typography.titleLarge
            )
            HorizontalDivider(thickness = 2.dp)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (student?.majorSubject != null) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Schwerpunkt")
                        Text(
                            text = student.majorSubject
                        )
                    }
                }
                if (student?.supplementarySubject != "") {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ergänzungsfach")
                        Text(
                            text = student?.supplementarySubject ?: "-"
                        )
                    }
                }
                if (student?.artSubject != "") {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Kunstfach")
                        Text(
                            text = student?.artSubject ?: "-"
                        )
                    }
                }
                if (student?.thirdLanguage != "") {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Dritte Sprache")
                        Text(
                            text = student?.thirdLanguage ?: "-"
                        )
                    }
                }
                if (student?.mint == true) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("MINT")
                        Text(
                            text = "Ja"
                        )
                    }
                }
                if (student?.musicalInstrument != "") {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Musikinstrument")
                        Text(
                            text = student?.musicalInstrument ?: "-"
                        )
                    }
                }
                if (student?.additionalLanguage != "") {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Zusätzliche Sprache")
                        Text(
                            text = student?.additionalLanguage ?: "-"
                        )
                    }
                }
            }
        }
    }
}