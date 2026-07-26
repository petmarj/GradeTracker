package com.example.gradetracker.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Grade(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val timeCreated: Long = System.currentTimeMillis(),
    val subjectId: String?,
    var name: String,
    var value: Double,
    var weight: Double = 1.0,
)

enum class GradeSort {
    NEWEST,
    OLDEST,
    VALUE_ASC,
    VALUE_DESC,
    NAME
}
fun gradeColor(grade: Double?): Color {
    if (grade == null) return Color(0x00000000)
    val safeGrade = grade.coerceIn(1.0, 6.0)

    val red = Color(0xFFFF0000)
    val yellow = Color(0xFFFFFF00)
    val green = Color(0xFF00F000)

    return when {
        grade <= 2.0 -> red

        grade <= 4.5 -> {
            // 2.0 = Rot, 4.5 = Gelb
            val progress = ((grade - 2.0) / (4.5 - 2.0)).toFloat()
            lerp(red, yellow, progress)
        }

        grade < 6.0 -> {
            // 4.5 = Gelb, 6.0 = Grün
            val progress = ((grade - 4.5) / (6.0 - 4.5)).toFloat()
            lerp(yellow, green, progress)
        }

        else -> green
    }
}