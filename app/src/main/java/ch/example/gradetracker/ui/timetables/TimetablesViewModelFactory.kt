package ch.example.gradetracker.ui.timetables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gradetracker.data.repository.LerbermattRepository

class TimetablesViewModelFactory(
    private val lerbermattRepository: LerbermattRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(TimetablesViewModel::class.java)) {
            return TimetablesViewModel(lerbermattRepository) as T
        }

        throw IllegalArgumentException(
            "Unbekannte ViewModel-Klasse: ${modelClass.name}"
        )
    }
}