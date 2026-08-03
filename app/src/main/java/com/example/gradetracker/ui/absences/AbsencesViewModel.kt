package com.example.gradetracker.ui.absences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gradetracker.data.repository.StudentRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class AbsencesViewModel(
    private val studentRepository: StudentRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(AbsencesUiState())
    val uiState: StateFlow<AbsencesUiState> = _uiState.asStateFlow()
    private var loadingJob: Job? = null

    init {
        observeUnreadAbsences()
        getData()
    }

    private fun observeUnreadAbsences() {
        viewModelScope.launch {
            studentRepository.observeUnreadAbsenceIds().collect { unreadIds ->
                _uiState.update {
                    it.copy(unreadAbsenceIds = unreadIds)
                }
            }
        }
    }

    fun markAbsenceAsRead(absenceId: Int) {
        viewModelScope.launch {
            studentRepository.markAbsenceAsRead(absenceId)
        }
    }

    fun markAbsencesAsRead(absenceType: Int) {
        val absenceIds = _uiState.value.absences
            .orEmpty()
            .asSequence()
            .filter { absence -> absence.type == absenceType }
            .map { absence -> absence.id }
            .filter { absenceId ->
                absenceId in _uiState.value.unreadAbsenceIds
            }
            .toList()

        if (absenceIds.isEmpty()) return

        viewModelScope.launch {
            studentRepository.markAbsencesAsRead(absenceIds)
        }
    }

     fun getData(isRefresh: Boolean = false){
        loadingJob?.cancel()

        loadingJob = viewModelScope.launch{
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isRefreshing = isRefresh,
                    errorMessage = null
                )
            }
            try {
                val response = studentRepository.getAbsences()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        absences = response.data.absences,
                        isRefreshing = false
                    )
                }
            }
            catch (exception: IOException) {
                showError("Keine Internetverbindung")
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
