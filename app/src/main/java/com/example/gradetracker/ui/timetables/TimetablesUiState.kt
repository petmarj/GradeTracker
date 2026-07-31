package com.example.gradetracker.ui.timetables

import com.example.gradetracker.model.TimetableLink
import com.example.gradetracker.model.TimetableLinks

data class TimetablesUiState (
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val timetableLinks: List<TimetableLink> = emptyList()
)