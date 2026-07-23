package com.example.gradetracker.ui.theme.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gradetracker.repo.SchedulerRepository
import com.example.gradetracker.data.API.SchedulerResponse
import kotlinx.coroutines.launch

class SchedulerViewModel(
    private val repository: SchedulerRepository
) : ViewModel() {

    var schedulerResponse by mutableStateOf<SchedulerResponse?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadScheduler(
        token: String,
        from: String,
        to: String
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                schedulerResponse = repository.getSchedule(
                    token = token,
                    from = from,
                    to = to
                )
            } catch (exception: Exception) {
                errorMessage =
                    exception.message ?: "Unbekannter Fehler"
            } finally {
                isLoading = false
            }
        }
    }
}