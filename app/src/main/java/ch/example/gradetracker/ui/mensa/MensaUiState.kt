package ch.example.gradetracker.ui.mensa

import com.example.gradetracker.model.MensaWeek
import java.time.LocalDate

data class MensaUiState(
    val menu: MensaWeek? = null,
    val weekStart: LocalDate,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
