package ch.example.gradetracker.ui.timetables

import com.example.gradetracker.model.TimetableLink

data class TimetablesUiState(
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val timetableLinks: List<TimetableLink> = emptyList()
)