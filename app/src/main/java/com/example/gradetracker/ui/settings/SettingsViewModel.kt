package com.example.gradetracker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gradetracker.data.preferences.SortPreferences
import com.example.gradetracker.data.remote.TokenStore
import com.example.gradetracker.data.repository.SchedulerRepository
import com.example.gradetracker.model.GradeSort
import com.example.gradetracker.model.SubjectSort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

class SettingsViewModel(
    private val tokenStore: TokenStore,
    private val schedulerRepository: SchedulerRepository,
    private val sortPreferences: SortPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        checkToken()
        testConnection()
        viewModelScope.launch {
            sortPreferences.subjectSort.collect { sort ->
                _uiState.update {
                    it.copy(subjectSort = sort)
                }
            }
        }

        viewModelScope.launch {
            sortPreferences.gradeSort.collect { sort ->
                _uiState.update {
                    it.copy(gradeSort = sort)
                }
            }
        }
    }

    private fun checkToken() {
        viewModelScope.launch {
            val token = tokenStore.getToken()

            _uiState.update {
                it.copy(tokenConfigured = !token.isNullOrBlank())
            }
        }
    }
    fun setSubjectSort(sort: SubjectSort) {
        sortPreferences.setSubjectSort(sort)
    }

    fun setGradeSort(sort: GradeSort) {
        sortPreferences.setGradeSort(sort)
    }

    fun storeToken(token: String){
        CoroutineScope(
            Dispatchers.IO
        ).launch {
            tokenStore.saveToken(token)
            _uiState.update {
                it.copy(
                    tokenConfigured = true,
                    connectionState = ConnectionState.NotTested
                )

            }
            testConnection()
        }


    }
     fun deleteToken(){
         CoroutineScope(
             Dispatchers.IO
         ).launch {
             tokenStore.clearToken()
             _uiState.update {
                 it.copy(
                     tokenConfigured = false,
                     connectionState = ConnectionState.MissingToken
                 )
             }
         }


    }

    fun testConnection() {
        viewModelScope.launch {
            val token = tokenStore.getToken()

            if (token.isNullOrBlank()) {
                _uiState.update {
                    it.copy(
                        tokenConfigured = false,
                        connectionState = ConnectionState.MissingToken
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(connectionState = ConnectionState.Testing)
            }

            val zone = ZoneId.of("Europe/Zurich")
            val today = LocalDate.now(zone)

            val monday = today.with(
                TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
            )
            val friday = monday.plusDays(4)

            try {
                schedulerRepository.getSchedule(
                    from = monday.toString(),
                    to = friday.toString()
                )

                _uiState.update {
                    it.copy(connectionState = ConnectionState.Connected)
                }
            } catch (exception: HttpException) {
                val message = when (exception.code()) {
                    401 -> "Token ist ungültig oder abgelaufen"
                    403 -> "Zugriff wurde verweigert"
                    else -> "API Fehler, HTTP ERROR: ${exception.code()}"
                }

                _uiState.update {
                    it.copy(connectionState = ConnectionState.Failed(message))
                }
            } catch (exception: IOException) {
                _uiState.update {
                    it.copy(
                        connectionState = ConnectionState.Failed(
                            "Verbindung konnte nicht hergestellt werden"
                        )
                    )
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        connectionState = ConnectionState.Failed(
                            exception.message
                                ?: "Ein unbekannter Fehler ist aufgetreten"
                        )
                    )
                }
            }
        }
    }
}
