package com.example.gradetracker.ui.student

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AbsencesCard(
    state: StudentUiState
) {
    val student = state.student
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(10.dp),

        ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Absenzen",
                style = MaterialTheme.typography.titleLarge
            )
            HorizontalDivider(thickness = 2.dp)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Entschuldigt")
                Text(
                    text = student?.absencesExcused.toString() ?: "-"
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Unentschuldigt")
                Text(
                    text = student?.absencesUnexcused.toString() ?: "-"
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Offen")
                Text(
                    text = student?.absencesOpen.toString() ?: "-"
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Verspätungen")
                Text(
                    text = student?.delays.toString() ?: "-"
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Halbtage")
                Text(
                    text = student?.halfDaysUsed.toString() + " / " + state.maxHalfdays.toString()
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Dispensationen")
                Text(
                    text = student?.dispensations.toString() ?: "-"
                )
            }
        }
    }
}