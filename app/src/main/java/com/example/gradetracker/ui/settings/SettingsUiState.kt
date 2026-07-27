package com.example.gradetracker.ui.settings

import com.example.gradetracker.model.GradeSort
import com.example.gradetracker.model.SubjectSort

data class SettingsUiState(
    val tokenConfigured: Boolean = false,
    val connectionState: ConnectionState = ConnectionState.NotTested,
    val subjectSort: SubjectSort = SubjectSort.NEWEST,
    val gradeSort: GradeSort = GradeSort.NEWEST
)

sealed interface ConnectionState {
    data object NotTested : ConnectionState
    data object Testing : ConnectionState
    data object Connected : ConnectionState
    data object MissingToken : ConnectionState
    data class Failed(val message: String) : ConnectionState
}