package com.example.gradetracker.ui.theme.components

import com.example.gradetracker.data.API.SchedulerResponse
import org.jetbrains.annotations.Async

sealed interface SchedulerUiState {

    data object Idle : SchedulerUiState

    data object Loading : SchedulerUiState

    data class Success(
        val schedule: SchedulerResponse
    ) : SchedulerUiState

    data class Error(
        val message: String
    ) : SchedulerUiState
}