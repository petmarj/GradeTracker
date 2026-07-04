package com.example.gradetracker

import java.util.UUID



data class Grade(
    val id: String = UUID.randomUUID().toString(),
    val subjectId: String,
    var title: String,
    var value: Double,
    var weight: Double = 1.0,
    var date: Long  = System.currentTimeMillis()
)