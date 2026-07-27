package com.example.gradetracker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gradetracker.data.preferences.SortPreferences
import com.example.gradetracker.data.remote.TokenStore
import com.example.gradetracker.data.repository.SchedulerRepository
import com.example.gradetracker.ui.schedule.SchedulerViewModel

class SettingsViewModelFactory(
    private val tokenStore: TokenStore,
    private val schedulerRepository: SchedulerRepository,
    private val sortPreferences: SortPreferences
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(
                tokenStore,
                schedulerRepository,
                sortPreferences,
            ) as T
        }

        throw IllegalArgumentException(
            "Unbekannte ViewModel-Klasse: ${modelClass.name}"
        )
    }
}