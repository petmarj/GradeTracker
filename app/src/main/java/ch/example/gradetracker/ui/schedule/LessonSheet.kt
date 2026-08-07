package ch.example.gradetracker.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.gradetracker.model.Absence
import com.example.gradetracker.model.Exam
import com.example.gradetracker.model.Lesson
import com.example.gradetracker.model.LessonBaseState
import com.example.gradetracker.model.LessonVisualState
import com.example.gradetracker.model.Room
import com.example.gradetracker.model.getLessonVisualState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun LessonSheet(
    lesson: Lesson?,
    absences: List<Absence>,
    exams: List<Exam>
) {
    if (lesson == null) return

    val hasExam =
        exams.isNotEmpty() && exams.any { exam ->
            exam.lessonId == lesson.id
        }

    val absence: Absence? = absences.firstOrNull { absence ->
        absence.date == lesson.date &&
                absence.timeSlot.id == lesson.timeSlot.id
    }
    val visualState = getLessonVisualState(
        lesson = lesson,
        absences = absences,
        exams = exams
    )



    if (absence != null) {
        val comments = listOf(
            "Schüler/in" to absence.commentStudent,
            "Eltern" to absence.commentParent,
            "Lehrperson" to absence.commentTeacher,
            "Intern" to absence.commentIntern
        ).filter { (_, comment) ->
            !comment.isNullOrBlank()
        }
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 32.dp
            )
            .verticalScroll(rememberScrollState())
    ) {
        BasicCard(lesson, hasExam, visualState)
        if (absence != null) {
            AbsenceCard(
                absence = absence,
                lesson = lesson
            )
        }
        RoomCard(lesson.lessonRooms.firstOrNull()?.room ?: null)
    }


}

@Composable
private fun BasicCard(
    lesson: Lesson,
    hasExam: Boolean,
    visualState: LessonVisualState
) {
    val formattedDate = remember(lesson.date) {
        runCatching {
            LocalDate.parse(lesson.date.take(10))
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        }.getOrDefault(lesson.date)
    }

    val timeText = lesson.timeSlot.let { timeSlot ->
        "${formatTime(timeSlot.startTime)} – ${formatTime(timeSlot.endTime)}"
    } ?: "Keine Zeit gefunden"
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()

    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = lesson.subject.name
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
            lesson.teacher.let { teacher ->
                InformationRow(
                    label = "Lehrperson",
                    value = "${teacher.firstname} ${teacher.lastname}"
                )
            }
            InformationRow(
                label = "Raum",
                value = lesson.lessonRooms.firstOrNull()?.room?.namedId?.drop(3) ?: "-"
            )

            InformationRow(
                label = "Status",
                value = when (visualState.baseState) {
                    LessonBaseState.PLANNED -> "Geplant"
                    LessonBaseState.CANCELLED -> "Ausfall"
                    LessonBaseState.COMPLETED -> "Beendet"
                }
            )
            if (hasExam) {
                InformationRow(
                    label = "Test?",
                    value = "Ja"
                )
            }
        }
    }
}

@Composable
private fun RoomCard(
    room: Room?
) {
    if (room == null) return

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()

    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = room.namedId.drop(3),
                style = MaterialTheme.typography.headlineSmall
            )
            HorizontalDivider()

            InformationRow(
                label = "Leiter*in",
                value = room.commandingTeacher ?: "-"
            )

            InformationRow(
                label = "Fach",
                value = room.subjectName
            )
        }
    }
}

@Composable
private fun AbsenceCard(
    absence: Absence,
    lesson: Lesson
) {
    val typeText = when (absence.type) {
        0 -> "Ungeplante Absenz"
        1 -> "Geplante Absenz"
        2 -> "Verspätung"
        3 -> "Dispensation"
        5 -> "Halbtag"
        else -> "Absenz"
    }


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
                        .size(20.dp)
                        .background(
                            color = statusColor,
                            shape = CircleShape
                        )
                )

                Text(
                    text = typeText,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }


            HorizontalDivider()



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
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    //onOpenWebsite(absence.id)
                }
            ) {
                Text("Absenz bearbeiten")
            }
        }
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