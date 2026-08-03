package com.example.gradetracker.ui.mensa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gradetracker.data.repository.MensaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

class MensaViewModel(
    private val repository: MensaRepository
) : ViewModel() {


    private val zurichZone = ZoneId.of("Europe/Zurich")
    private val currentMonday: LocalDate
        get() = LocalDate
            .now(zurichZone)
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    private val _uiState = MutableStateFlow(
        MensaUiState(
            weekStart = currentMonday
        )
    )
    val uiState: StateFlow<MensaUiState> =
        _uiState.asStateFlow()

    private var loadingJob: Job? = null

    init {
        loadWeek()
    }

    fun previousWeek() {
        val newMonday = _uiState.value.weekStart.minusWeeks(1)

        _uiState.update {
            it.copy(weekStart = newMonday)
        }

        loadWeek()
    }

    fun nextWeek() {
        val newMonday = _uiState.value.weekStart.plusWeeks(1)

        if (newMonday > currentMonday.plusWeeks(1)) return
        _uiState.update {
            it.copy(weekStart = newMonday)
        }

        loadWeek()
    }

    fun currentWeek() {
        _uiState.update {
            it.copy(weekStart = currentMonday)
        }

        loadWeek()
    }

    private fun loadWeek() {
        loadingJob?.cancel()

        loadingJob = viewModelScope.launch {
            val weekStart = _uiState.value.weekStart

            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            try {
                val response = repository.getMenu(weekStart)

                if (_uiState.value.weekStart == weekStart) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            menu = response
                        )
                    }
                }
            } catch (exception: IOException) {
                showError("Keine Verbindung zur API.")
            } catch (exception: HttpException) {
                val message = when (exception.code()) {
                    401 -> "Token ist ungültig oder abgelaufen."
                    403 -> "Keine Berechtigung für das Menü."
                    else -> "API Fehler, HTTP ERROR: ${exception.code()}."
                }

                showError(message)
            } catch (exception: Exception) {
                showError(
                    exception.message ?: "Das Menü konnte nicht geladen werden."
                )
            }
        }
    }

    private fun showError(message: String) {
        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = message
            )
        }
    }
}