package com.example.gradetracker.ui.schedule

import com.example.gradetracker.data.remote.model.SchedulerResponse
import com.example.gradetracker.model.HolidayType
import java.time.LocalDate

data class SchedulerUiState(
    val weekStart: LocalDate,
    val weekEnd: LocalDate,
    val schedule: SchedulerResponse? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val holiday: HolidayType? = null
)