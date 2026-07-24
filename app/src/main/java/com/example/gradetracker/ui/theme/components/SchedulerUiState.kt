package com.example.gradetracker.ui.theme.components

import com.example.gradetracker.data.API.SchedulerResponse
import com.example.gradetracker.data.HolidayType
import org.jetbrains.annotations.Async
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