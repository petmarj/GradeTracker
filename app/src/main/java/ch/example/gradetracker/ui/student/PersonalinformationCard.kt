package ch.example.gradetracker.ui.student

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun PersonalinformationCard(
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
                Text("Geburtstag")
                Text(
                    text = LocalDateTime
                        .parse(student?.birthdate)
                        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                )

            }
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Adresse")
                Text(
                    text = student?.address?.addressLine1 + (student?.address?.addressLine2
                        ?: "") + ", " + student?.address?.zipCode + ", " + student?.address?.city
                )

            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Staatsangehörigkeit")
                Text(
                    text = student?.nationality ?: "-"
                )

            }

        }
    }
}