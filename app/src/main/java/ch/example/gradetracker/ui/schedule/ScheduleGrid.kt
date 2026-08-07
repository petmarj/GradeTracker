package ch.example.gradetracker.ui.schedule

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.gradetracker.model.Absence
import com.example.gradetracker.model.Exam
import com.example.gradetracker.model.Lesson
import com.example.gradetracker.model.scheduleTimeSlots
import java.time.LocalDate

@Composable
fun ScheduleGrid(
    weekStart: LocalDate,
    lessonsByDay: Map<Int, List<Lesson>>,
    absences: List<Absence>,
    exams: List<Exam>,
    onClick: (Lesson) -> Unit
) {
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(horizontalScrollState)
            .verticalScroll(verticalScrollState)
    ) {
        ScheduleDayHeaderRow(weekStart)

        scheduleTimeSlots.forEach { timeSlot ->
            Row {
                TimeCell(timeSlot)

                for (dayOfWeek in 1..5) {
                    val lesson = lessonsByDay[dayOfWeek]
                        .orEmpty()
                        .firstOrNull {
                            it.timeSlot.startTime == timeSlot.startTime
                        }

                    LessonCell(
                        lesson = lesson,
                        absences = absences,
                        exams = exams,
                        onClick = onClick
                    )
                }
            }
        }
    }
}

