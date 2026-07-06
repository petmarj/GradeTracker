package com.example.gradetracker.data

import java.util.UUID

data class Grade(
    val id: String = UUID.randomUUID().toString(),
    val subjectId: String?,
    var name: String,
    var value: Double,
    var weight: Double = 1.0,
)