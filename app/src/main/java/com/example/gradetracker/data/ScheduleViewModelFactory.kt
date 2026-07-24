package com.example.gradetracker.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gradetracker.repo.SchedulerRepository
import com.example.gradetracker.ui.theme.components.SchedulerViewModel

class SchedulerViewModelFactory(
    private val repository: SchedulerRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(SchedulerViewModel::class.java)) {
            return SchedulerViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unbekannte ViewModel-Klasse: ${modelClass.name}"
        )
    }
}