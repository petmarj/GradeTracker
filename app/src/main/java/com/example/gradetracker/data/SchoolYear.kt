package com.example.gradetracker.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import java.util.UUID
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gradetracker.logic.Calculator

@Entity
data class SchoolYear (
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val timeCreated: Long = System.currentTimeMillis(),
    val name: String
)


fun schoolYearColor(
    subjects: List<Subject>,
    grades: List<Grade>
): Color {
    val validSubjectCount = subjects.count { subject ->
        val subjectGrades = grades.filter { it.subjectId == subject.id }
        Calculator.getAverageForGrades(subjectGrades) != null
    }

    if (validSubjectCount == 0) {
        return Color(0x00000000)
    }

    val points = Calculator.getPointsForSchoolYear(
        grades = grades,
        subjects = subjects
    ) ?: return Color(0x00000000)

    val maximum = validSubjectCount * 2.0
    val minimum = validSubjectCount * -6.0

    val yellowAt = maximum * 0.25
    val redAt = maximum * -0.25

    val safePoints = points.coerceIn(minimum, maximum)

    val red = Color(0xFFFF0000)
    val yellow = Color(0xFFFFFF00)
    val green = Color(0xFF00FF00)

    return when {
        safePoints <= redAt -> red

        safePoints <= yellowAt -> {
            val progress =
                ((safePoints - redAt) / (yellowAt - redAt)).toFloat()

            lerp(red, yellow, progress)
        }

        else -> {
            val progress =
                ((safePoints - yellowAt) / (maximum - yellowAt)).toFloat()

            lerp(yellow, green, progress)
        }
    }
}