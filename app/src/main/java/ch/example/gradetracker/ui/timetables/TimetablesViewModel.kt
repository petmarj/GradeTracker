package ch.example.gradetracker.ui.timetables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gradetracker.data.repository.LerbermattRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class TimetablesViewModel(
    private val lerbermattRepository: LerbermattRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TimetablesUiState())
    val uiState: StateFlow<TimetablesUiState> = _uiState.asStateFlow()
    private var loadingJob: Job? = null

    init {
        getLinks()
    }

    private fun getLinks() {
        loadingJob?.cancel()

        loadingJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }
            try {
                val response = lerbermattRepository.getTimetableLinks()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        timetableLinks = response
                    )
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
                errorMessage = message
            )
        }
    }
}