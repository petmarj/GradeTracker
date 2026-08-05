package com.example.gradetracker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gradetracker.data.preferences.AppPreferences
import com.example.gradetracker.data.remote.TokenStore
import com.example.gradetracker.data.repository.StudentRepository
import com.example.gradetracker.data.repository.GradeRepository

class SettingsViewModelFactory(
    private val tokenStore: TokenStore,
    private val studentRepository: StudentRepository,
    private val appPreferences: AppPreferences,
    private val gradeRepository: GradeRepository
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
                gradeRepository,
            ) as T
        }

        throw IllegalArgumentException(
            "Unbekannte ViewModel-Klasse: ${modelClass.name}"
        )
    }
}
