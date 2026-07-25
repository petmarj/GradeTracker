package com.example.gradetracker.ui.theme.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gradetracker.data.Absence
import com.example.gradetracker.data.Exam
import com.example.gradetracker.data.Lesson
import com.example.gradetracker.data.scheduleTimeSlots
import com.example.gradetracker.repo.SchedulerRepository
import java.time.LocalDate

@Composable
fun ScheduleGrid(
    weekStart: LocalDate,
    lessonsByDay: Map<Int, List<Lesson>>,
    absences: List<Absence>,
    exams: List<Exam>
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
                            it.timeslot.startTime == timeSlot.startTime
                        }

                    LessonCell(lesson = lesson, absences = absences, exams = exams)
                }
            }
        }
    }
}

