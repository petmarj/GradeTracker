package ch.example.gradetracker.model

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
    VALUE_DESC,
    NAME
}

fun gradeColor(
    grade: Double?,
    mode: GradeColorMode = GradeColorMode.NORMAL
): Color {
    if (grade == null) return Color(0x00000000)
    val safeGrade = grade.coerceIn(1.0, 6.0)

    val red = Color(0xFFFF0000)
    val yellow = Color(0xFFFFFF00)
    val green = Color(0xFF00F000)

    return when {
        safeGrade <= mode.redUntil -> red

        safeGrade <= mode.yellowAt -> {
            val progress = (
                    (safeGrade - mode.redUntil) /
                            (mode.yellowAt - mode.redUntil)
                    ).toFloat()
            lerp(red, yellow, progress)
        }

        safeGrade < 6.0 -> {
            val progress = (
                    (safeGrade - mode.yellowAt) /
                            (6.0 - mode.yellowAt)
                    ).toFloat()
            lerp(yellow, green, progress)
        }

        else -> green
    }
}
