package ch.example.gradetracker.ui.schedule

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.gradetracker.model.Absence
import com.example.gradetracker.model.Exam
import com.example.gradetracker.model.Lesson
import com.example.gradetracker.model.LessonBaseState
import com.example.gradetracker.model.colorsForBaseState
import com.example.gradetracker.model.getLessonVisualState

@Composable
fun LessonCell(
    lesson: Lesson?,
    absences: List<Absence>,
    exams: List<Exam>,
    onClick: (Lesson) -> Unit
) {
    Box(
        modifier = Modifier
            .width(130.dp)
            .height(55.dp)
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            .padding(2.dp)
    ) {
        if (lesson != null) {
            val visualState = getLessonVisualState(
                lesson = lesson,
                absences = absences,
                exams = exams
            )
            val colors = colorsForBaseState(
                visualState.baseState
            )
            val statusDescription = buildList {
                when (visualState.baseState) {
                    LessonBaseState.PLANNED -> add("Geplant")
                    LessonBaseState.COMPLETED -> add("Abgeschlossen")
                    LessonBaseState.CANCELLED -> add("Abgesagt")
                }

                if (visualState.hasExam) {
                    add("Prüfung")
                }

                if (visualState.hasAbsence) {
                    add("Absenz")
                }
            }.joinToString()

            Surface(
                modifier = Modifier.fillMaxSize().semantics {
                    contentDescription =
                        "${lesson.subject.name}, $statusDescription"
                }
                    .combinedClickable(
                        onClick = { onClick(lesson) }
                    ),
                color = colors.background,
                shape = RoundedCornerShape(5.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = colors.border
                )

            ) {
                Box {
                    Column(
                        modifier = Modifier.padding(6.dp)

                    ) {

                        Text(
                            text = lesson.subject.name,
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = colors.content
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(end = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,

                            ) {

                            Text(
                                text = lesson.teacher.namedId.drop(3),
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = colors.content
                            )

                            Text(
                                text = lesson.lessonRooms.firstOrNull()?.room?.namedId?.drop(3)
                                    ?: "-",
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = colors.content
                            )

                        }


                    }
                }

                LessonCornerMarkers(
                    hasExam = visualState.hasExam,
                    hasAbsence = visualState.hasAbsence,
                    modifier = Modifier.matchParentSize(),
                    absenceIsUnplanned = visualState.absenceIsUnplanned
                )

            }
        }
    }
}

@Composable
private fun LessonCornerMarkers(
    hasExam: Boolean,
    hasAbsence: Boolean,
    absenceIsUnplanned: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val cornerSize = 24.dp.toPx()

        if (hasExam) {
            val examCorner = Path().apply {
                moveTo(size.width - cornerSize, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, cornerSize)
                close()
            }

            drawPath(
                path = examCorner,
                color = Color(0xFF8E5CC2)
            )
        }

        if (hasAbsence && !absenceIsUnplanned) {
            val absenceCorner = Path().apply {
                moveTo(size.width, size.height - cornerSize)
                lineTo(size.width, size.height)
                lineTo(size.width - cornerSize, size.height)
                close()
            }

            drawPath(
                path = absenceCorner,
                color = Color(0xFF045fcf)
            )
        }

        if (hasAbsence && absenceIsUnplanned) {
            val absenceCorner = Path().apply {
                moveTo(size.width, size.height - cornerSize)
                lineTo(size.width, size.height)
                lineTo(size.width - cornerSize, size.height)
                close()
            }

            drawPath(
                path = absenceCorner,
                color = Color(0xFFFF0000)
            )
        }
    }
}