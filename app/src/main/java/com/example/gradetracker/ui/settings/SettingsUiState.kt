package com.example.gradetracker.ui.settings

import com.example.gradetracker.data.remote.model.User
import com.example.gradetracker.model.GradeColorMode
import com.example.gradetracker.model.GradeSort
import com.example.gradetracker.model.SubjectSort

data class SettingsUiState(
    val loggedIn: Boolean = false,
    val connectionState: ConnectionState = ConnectionState.NotLoggedIn,
    val user: User? = null,
    val subjectSort: SubjectSort = SubjectSort.NEWEST,
    val gradeSort: GradeSort = GradeSort.NEWEST,
    val gradeColorMode: GradeColorMode = GradeColorMode.NORMAL,
    val plusPointsImportState: PlusPointsImportState = PlusPointsImportState.Idle
)

sealed interface PlusPointsImportState {
    data object Idle : PlusPointsImportState
    data object Importing : PlusPointsImportState
    data class Success(
        val semesterName: String,
        val subjectCount: Int,
        val gradeCount: Int
    ) : PlusPointsImportState
    data class Failed(val message: String) : PlusPointsImportState
}

sealed interface ConnectionState {
    data object NotTested : ConnectionState
    data object Testing : ConnectionState
    data object NotLoggedIn : ConnectionState
    data object Connected : ConnectionState
    data class Failed(val message: String) : ConnectionState
}
