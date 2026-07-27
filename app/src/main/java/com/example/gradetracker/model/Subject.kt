package com.example.gradetracker.model

import java.util.UUID
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Subject (
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