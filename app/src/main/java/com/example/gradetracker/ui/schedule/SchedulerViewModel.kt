package com.example.gradetracker.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gradetracker.data.repository.SchedulerRepository
import com.example.gradetracker.model.holidayForWeek
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


class SchedulerViewModel(
    private val repository: SchedulerRepository
) : ViewModel() {


    private val zurichZone = ZoneId.of("Europe/Zurich")
    private val currentMonday: LocalDate
        get() = LocalDate
            .now(zurichZone)
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    private val currentFriday: LocalDate
        get() = currentMonday.plusDays(4)

    private val _uiState = MutableStateFlow(
        SchedulerUiState(
            weekStart = currentMonday,
            weekEnd = currentFriday
        )
    )
    val uiState: StateFlow<SchedulerUiState> =
        _uiState.asStateFlow()

    private var loadingJob: Job? = null

    init {
        loadWeek()
    }
    fun previousWeek() {
        val newWeekStart = _uiState.value.weekStart.minusWeeks(1)
        val newWeekEnd = _uiState.value.weekEnd.minusWeeks(1)

        _uiState.update {
            it.copy(weekStart = newWeekStart, weekEnd = newWeekEnd)

        }

        loadWeek()
    }

    fun nextWeek() {
        val newWeekStart = _uiState.value.weekStart.plusWeeks(1)
        val newWeekEnd = _uiState.value.weekEnd.plusWeeks(1)

        _uiState.update {
            it.copy(weekStart = newWeekStart, weekEnd = newWeekEnd)
        }

        loadWeek()
    }
    fun currentWeek() {
        _uiState.update {
            it.copy(weekStart = currentMonday, weekEnd = currentFriday)
        }

        loadWeek()
    }
    fun refresh() {
        loadWeek(isRefresh = true)
    }
    private fun loadWeek(isRefresh: Boolean = false) {
        loadingJob?.cancel()

        loadingJob = viewModelScope.launch {
            val weekStart = _uiState.value.weekStart

            val weekEnd = _uiState.value.weekEnd

            val holiday = holidayForWeek(weekStart)

            if (holiday != null){
                _uiState.update {
                    it.copy(
                        holiday = holiday,
                        schedule = null,
                        isLoading = false,
                        errorMessage = null
                    )
                }
                return@launch
            }
            _uiState.update {
                it.copy(
                    holiday = null,
                    isLoading = !isRefresh,
                    isRefreshing = isRefresh,
                    errorMessage = null
                )
            }

            try {
                val response = repository.getSchedule(
                    from = "$weekStart",
                    to = "$weekEnd"
                )

                if (_uiState.value.weekStart == weekStart) {
                    _uiState.update {
                        it.copy(
                            holiday = null,
                            schedule = response,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
            } catch (exception: IOException) {
                showError("Keine Verbindung zur API.")
            } catch (exception: HttpException) {
                val message = when (exception.code()) {
                    401 -> "Token ist ungültig oder abgelaufen."
                    403 -> "Keine Berechtigung für den Stundenplan."
                    else -> "API Fehler, HTTP ERROR: ${exception.code()}."
                }

                showError(message)
            } catch (exception: Exception) {
                showError(
                    exception.message ?: "Der Stundenplan konnte nicht geladen werden."
                )
            }
        }
    }
    private fun showError(message: String) {
        _uiState.update {
            it.copy(
                isLoading = false,
                isRefreshing = false,
                errorMessage = message
            )
        }
    }

}