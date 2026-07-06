package com.example.gradetracker.data

import java.util.UUID
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Subject (
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val schoolYearId: String?
)