package com.example.gradetracker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gradetracker.data.preferences.AppPreferences
import com.example.gradetracker.data.remote.TokenStore
import com.example.gradetracker.data.remote.model.User
import com.example.gradetracker.data.repository.StudentRepository
import com.example.gradetracker.model.GradeColorMode
import com.example.gradetracker.model.GradeSort
import com.example.gradetracker.model.SubjectSort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class SettingsViewModel(
    private val tokenStore: TokenStore,
    private val studentRepository: StudentRepository,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        checkToken()
        getUser()
        viewModelScope.launch {
            appPreferences.settings.collect { settings ->
                _uiState.update {
                    it.copy(
                        subjectSort = settings.subjectSort,
                        gradeSort = settings.gradeSort,
                        gradeColorMode = settings.gradeColorMode
                    )
                }
            }
        }
    }

    private fun getUser() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    connectionState = ConnectionState.Testing
                )
            }

            try {
                val response = studentRepository.getStudentData()

                val student = response.data.student

                val user = User(
                    username = student.studentRefId.toString(),
                    firstname = student.firstname,
                    lastname = student.lastname,
                    studentId = student.id.toString(),
                    id = ""
                )

                _uiState.update {
                    it.copy(
                        connectionState = ConnectionState.Connected,
                        user = user,
                        loggedIn = true
                    )
                }
            } catch (exception: HttpException) {
                val message = when (exception.code()) {
                    400, 401 ->
                        "Nicht angemeldet"

                    else ->
                        "Login fehlgeschlagen (HTTP ${exception.code()})."
                }

                _uiState.update {
                    it.copy(
                        loggedIn = false,
                        connectionState = ConnectionState.NotLoggedIn
                    )
                }
            } catch (exception: IOException) {
                _uiState.update {
                    it.copy(
                        loggedIn = false,
                        connectionState = ConnectionState.Failed(
                            "Keine Verbindung zum Absenzensystem."
                        )
                    )
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        loggedIn = false,
                        connectionState = ConnectionState.Failed(
                            exception.message
                                ?: "Der Login ist fehlgeschlagen."
                        )
                    )
                }
            }
        }
    }
    private fun checkToken() {
        viewModelScope.launch {
            val token = tokenStore.getToken()

            _uiState.update {
                it.copy(loggedIn = !token.isNullOrBlank())
            }
        }
    }
    fun setSubjectSort(sort: SubjectSort) {
        appPreferences.setSubjectSort(sort)
    }

    fun setGradeSort(sort: GradeSort) {
        appPreferences.setGradeSort(sort)
    }

    fun setGradeColorMode(mode: GradeColorMode) {
        appPreferences.setGradeColorMode(mode)
    }


    fun logout(){
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    loggedIn = false,
                    connectionState = ConnectionState.NotLoggedIn,
                    user = null
                )
            }

            tokenStore.clearToken()
        }
    }
    fun login(username: String, password: String) {
        val cleanUsername = username.trim()
        if (cleanUsername.isBlank() || password.isBlank()) {
            _uiState.update {
                it.copy(
                    connectionState = ConnectionState.Failed(
                        "Benutzername und Passwort sind erforderlich."
                    )
                )
            }
            return
        }
        if (_uiState.value.connectionState == ConnectionState.Testing) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    connectionState = ConnectionState.Testing
                )
            }

            try {
                val response = studentRepository.login(
                    username = cleanUsername,
                    password = password
                )

                val token = response.data.jwtToken
                    .trim()
                    .takeIf { it.isNotEmpty() }
                    ?: throw IllegalStateException(
                        "Die API hat keinen Login-Token geliefert."
                    )

                tokenStore.saveToken(token)


                _uiState.update {
                    it.copy(
                        loggedIn = true,
                        connectionState = ConnectionState.Connected,
                        user = response.data.user
                    )
                }
            } catch (exception: HttpException) {
                val message = when (exception.code()) {
                    400, 401 ->
                        "Benutzername oder Passwort ist falsch."

                    403 ->
                        "Der Zugriff wurde verweigert."

                    else ->
                        "Login fehlgeschlagen (HTTP ${exception.code()})."
                }

                _uiState.update {
                    it.copy(
                        loggedIn = false,
                        connectionState = ConnectionState.Failed(message)
                    )
                }
            } catch (exception: IOException) {
                _uiState.update {
                    it.copy(
                        loggedIn = false,
                        connectionState = ConnectionState.Failed(
                            "Keine Verbindung zum Absenzensystem."
                        )
                    )
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        loggedIn = false,
                        connectionState = ConnectionState.Failed(
                            exception.message
                                ?: "Der Login ist fehlgeschlagen."
                        )
                    )
                }
            }
        }


    }
}
