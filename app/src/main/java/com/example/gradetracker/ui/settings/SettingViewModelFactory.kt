package com.example.gradetracker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gradetracker.data.preferences.AppPreferences
import com.example.gradetracker.data.remote.TokenStore
import com.example.gradetracker.data.repository.SchedulerRepository
import com.example.gradetracker.data.repository.StudentRepository

class SettingsViewModelFactory(
    private val tokenStore: TokenStore,
    private val studentRepository: StudentRepository,
    private val appPreferences: AppPreferences
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(
                tokenStore,
                studentRepository,
                appPreferences,
            ) as T
        }

        throw IllegalArgumentException(
            "Unbekannte ViewModel-Klasse: ${modelClass.name}"
        )
    }
}
