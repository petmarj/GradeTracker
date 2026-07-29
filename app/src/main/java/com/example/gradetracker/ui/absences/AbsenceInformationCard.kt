package com.example.gradetracker.ui.absences

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.gradetracker.model.Absence
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.core.net.toUri

@Composable
fun AbsenceInformationCard(
    absence: Absence?
) {
    if (absence == null) return

    val typeText = when (absence.type) {
        0 -> "Ungeplante Absenz"
        1 -> "Geplante Absenz"
        2 -> "Verspätung"
        3 -> "Dispensation"
        else -> "Absenz"
    }

    val formattedDate = remember(absence.date) {
        runCatching {
            LocalDate.parse(absence.date.take(10))
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        }.getOrDefault(absence.date)
    }

    val timeText = absence.timeSlot.let { timeSlot ->
        "${formatTime(timeSlot.startTime)} – ${formatTime(timeSlot.endTime)}"
    } ?: "Keine bestimmte Lektion"

    val comments = listOf(
        "Schüler/in" to absence.commentStudent,
        "Eltern" to absence.commentParent,
        "Lehrperson" to absence.commentTeacher,
        "Intern" to absence.commentIntern
    ).filter { (_, comment) ->
        !comment.isNullOrBlank()
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 32.dp
            )
    ) {
        val statusColor = when (absence.state) {
            0 -> Color.Red
            1 -> Color.Yellow
            2 -> Color.Green
            else -> Color.Gray
        }
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            color = statusColor,
                            shape = CircleShape
                        )
                )

                Text(
                    text = typeText,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }


            Text(
                text = absence.lesson?.subject?.name
                    ?: "Keine bestimmte Lektion",
                style = MaterialTheme.typography.headlineSmall
            )

            HorizontalDivider()

            InformationRow(
                label = "Datum",
                value = formattedDate
            )

            InformationRow(
                label = "Zeit",
                value = timeText
            )

            absence.lesson?.teacher?.let { teacher ->
                InformationRow(
                    label = "Lehrperson",
                    value = "${teacher.firstname} ${teacher.lastname}"
                )
            }

            InformationRow(
                label = "Status",
                value = absenceStateText(absence.state)
            )


            if (comments.isNotEmpty()) {
                HorizontalDivider()

                Text(
                    text = "Kommentare",
                    style = MaterialTheme.typography.titleMedium
                )

                comments.forEach { (author, comment) ->
                    CommentBlock(
                        title = author,
                        comment = comment.orEmpty()
                    )
                }
            }
            HorizontalDivider()
            val context = LocalContext.current
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {

                    val url =
                        "https://absenzen.lerbermatt.ch/parent/confirmation/${absence.parentConfirmationGuid}"

                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        url.toUri()
                    )

                    context.startActivity(intent)
                }
            ) {
                Text("Absenz bearbeiten")
            }
        }
    }
}

@Composable
private fun InformationRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            modifier = Modifier.weight(1.5f),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun CommentBlock(
    title: String,
    comment: String
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = comment,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun absenceStateText(state: Int): String =
    when (state) {
        0 -> "Offen"
        1 -> "Beantragt"
        2 -> "Entschuldigt"
        else -> "Unbekannt"
    }

private fun formatTime(time: Int): String {
    val hours = time / 100
    val minutes = time % 100

    return "%02d:%02d".format(hours, minutes)
}