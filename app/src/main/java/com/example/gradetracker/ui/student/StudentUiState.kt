package com.example.gradetracker.ui.student

import android.graphics.Picture

import com.example.gradetracker.model.Student

data class StudentUiState(
    val student: Student? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val maxHalfdays: Int? = null
)