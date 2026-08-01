package com.example.gradetracker.model

import androidx.compose.ui.graphics.Color
import com.google.gson.annotations.SerializedName

data class Lesson (
    val id: Int,
    val date: String,
    val dayOfWeek: Int,
    val isCompleted: Boolean,
    val isCancelled: Boolean,
    val subject: LessonSubject,
    val teacher: Teacher,
    @SerializedName("timeslot")
    val timeSlot: TimeSlot,
    val lessonRooms: List<LessonRoom>,
    val exams: List<Exam>?
    ){

}

enum class LessonBaseState {
    PLANNED,
    COMPLETED,
    CANCELLED
}

data class LessonVisualState(
    val baseState: LessonBaseState,
    val hasExam: Boolean,
    val hasAbsence: Boolean,
    val absenceIsUnplanned: Boolean
)

data class LessonColors(
    val background: Color,
    val content: Color,
    val border: Color
)

fun colorsForBaseState(
    state: LessonBaseState
): LessonColors {
    return when (state) {
        LessonBaseState.PLANNED -> LessonColors(
            background = Color(0xFFDCEAFF),
            content = Color(0xFF000000),
            border = Color(0xFF7CACEC)
        )

        LessonBaseState.COMPLETED -> LessonColors(
            background = Color(0xFF00800d),
            content = Color(0xFFFFFFFF),
            border = Color(0xFF00800d)
        )

        LessonBaseState.CANCELLED -> LessonColors(
            background = Color(0xFFD45151),
            content = Color(0xFFFFFFFF),
            border = Color(0xFFD45151)
        )
    }
}
fun getLessonVisualState(
    lesson: Lesson,
    absences: List<Absence>,
    exams: List<Exam>
): LessonVisualState {
    val baseState = when {
        lesson.isCancelled -> LessonBaseState.CANCELLED
        lesson.isCompleted -> LessonBaseState.COMPLETED
        else -> LessonBaseState.PLANNED
    }

    val hasExam =
        lesson.exams?.isNotEmpty() == true ||
                exams.any { exam ->
                    exam.lessonId == lesson.id
                }

    val hasAbsence = absences.any { absence ->
        absence.date == lesson.date &&
                absence.timeSlot.id == lesson.timeSlot.id
    }

    val absenceIsUnplanned = absences.any { absence ->
        absence.date == lesson.date &&
                absence.timeSlot.id == lesson.timeSlot.id &&
                absence.type == 0
    }

    return LessonVisualState(
        baseState = baseState,
        hasExam = hasExam,
        hasAbsence = hasAbsence,
        absenceIsUnplanned = absenceIsUnplanned
    )
}
