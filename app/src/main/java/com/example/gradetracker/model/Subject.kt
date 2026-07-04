package com.example.gradetracker.model

import java.util.UUID

data class Subject (
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val schoolYearId: String
)