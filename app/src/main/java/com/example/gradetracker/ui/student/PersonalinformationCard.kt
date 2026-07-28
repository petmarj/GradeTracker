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
fun PersonalinformationCard(
    state: StudentUiState
){
    val student = state.student
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(10.dp),

        ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Persönliche Informationen",
                style = MaterialTheme.typography.titleLarge
            )
            HorizontalDivider(thickness = 2.dp)
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("E-Mail")
                Text(
                    text = student?.schoolEmail.toString() ?: "-"
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Adresse")
                Text(
                    text = student?.address?.addressLine1 + (student?.address?.addressLine2 ?: "") + ", " + student?.address?.zipCode + ", " + student?.address?.city
                )

            }

        }
    }
}