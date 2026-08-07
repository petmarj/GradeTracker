package ch.example.gradetracker.ui.absences

import com.example.gradetracker.model.Absence

data class AbsencesUiState(
    val absences: List<Absence>? = null,
    val unreadAbsenceIds: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false
)
