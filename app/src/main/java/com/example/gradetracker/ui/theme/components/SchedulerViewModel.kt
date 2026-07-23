package com.example.gradetracker.ui.theme.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gradetracker.data.API.NetworkClient
import com.example.gradetracker.repo.SchedulerRepository
import com.example.gradetracker.data.API.SchedulerResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class SchedulerViewModel(
    //private val repository: SchedulerRepository
) : ViewModel() {

    private val SchedulerRepository = SchedulerRepository(
        api = NetworkClient.lerbermattApi
    )
    var resultText by mutableStateOf("Noch nicht getestet")
        private set

    var schedulerResponse by mutableStateOf<SchedulerResponse?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun testApi(token: String) {
        if (token.isBlank()) {
            resultText = "Kein Token eingegeben."
            return
        }

        viewModelScope.launch {
            isLoading = true
            resultText = "API wird abgefragt …"

            try {
                val response = SchedulerRepository.getSchedule(
                    token = token,
                    from = "2026-06-08T00:00:00.000Z",
                    to = "2026-06-12T00:00:00.000Z"
                )

                val lessonsCount = response.data.lessons.size
                val absencesCount = response.data.absences.size
                val examsCount = response.data.exams.size
                val firstLesson = response.data.lessons.firstOrNull()

                resultText = if (firstLesson != null) {
                    """
                    Anfrage erfolgreich
                    Lektionen: ${response.data.lessons.size}
            
                    Erste Lektion:
                    Datum: ${firstLesson.date}
                    Fach: ${firstLesson.subject.name}
                    Lehrer: ${firstLesson.teacher.firstname} ${firstLesson.teacher.lastname}
                    Zeit: ${firstLesson.timeslot.startTime}–${firstLesson.timeslot.endTime}
                """.trimIndent()
                } else {
                    "Anfrage erfolgreich, aber keine Lektionen gefunden."
                }

            } catch (exception: HttpException) {
                resultText =
                    "HTTP-Fehler ${exception.code()}: ${exception.message()}"

            } catch (exception: IOException) {
                resultText =
                    "Netzwerkfehler: ${exception.message ?: "Keine Verbindung"}"

            } catch (exception: Exception) {
                resultText =
                    "Fehler: ${exception.message ?: exception::class.simpleName}"
            } finally {
                isLoading = false
            }
        }
    }

}