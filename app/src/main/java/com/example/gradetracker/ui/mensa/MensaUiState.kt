package com.example.gradetracker.ui.mensa

import com.example.gradetracker.model.MensaWeek
import java.time.LocalDate

data class MensaUiState(
    val menu: MensaWeek? = null,
    val weekStart: LocalDate,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
