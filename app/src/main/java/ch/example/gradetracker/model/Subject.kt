package ch.example.gradetracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Subject(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val timeCreated: Long = System.currentTimeMillis(),
    val name: String,
    val schoolYearId: String?
)

enum class SubjectSort {
    NEWEST,
    OLDEST,
    VALUE_DESC,
    NAME
}