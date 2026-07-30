package com.example.gradetracker.ui.absences

import android.graphics.Picture
import com.example.gradetracker.model.Absence
import com.example.gradetracker.model.Student

data class AbsencesUiState(
    val absences: List<Absence>? = null,
    val unreadAbsenceIds: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false
)
