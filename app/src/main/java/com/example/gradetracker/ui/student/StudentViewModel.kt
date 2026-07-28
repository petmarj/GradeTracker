package com.example.gradetracker.ui.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gradetracker.data.remote.TokenStore
import com.example.gradetracker.data.repository.SchedulerRepository
import com.example.gradetracker.data.repository.StudentRepository
import com.example.gradetracker.ui.settings.SettingsUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class StudentViewModel(
    private val studentRepository: StudentRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(StudentUiState())
    val uiState: StateFlow<StudentUiState> = _uiState.asStateFlow()
    private var loadingJob: Job? = null

    init {
        getData()
    }

    private fun getData(){
        loadingJob?.cancel()

        loadingJob = viewModelScope.launch{
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }
            try {
                val response = studentRepository.getStudentData()

                _uiState.update {
                    it.copy(
                        student = response.data.student,
                        isLoading = false
                    )
                }
            }
            catch (exception: IOException) {
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
                errorMessage = message
            )
        }
    }
}