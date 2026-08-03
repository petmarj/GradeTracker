package com.example.gradetracker.ui.student

import com.example.gradetracker.model.Student

data class StudentUiState(
    val student: Student? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val maxHalfdays: Int? = null
)