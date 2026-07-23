package com.example.gradetracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Grade(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val subjectId: String?,
    var name: String,
    var value: Double,
    var weight: Double = 1.0,
)