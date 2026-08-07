package ch.example.gradetracker.ui.absences

import android.util.Base64
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gradetracker.data.remote.TokenStore
import com.example.gradetracker.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject

object AbsenceRoutes {
    const val WEBSITE = "absences/absenceWebsite/{absenceId}"

    fun website(absenceId: Int): String =
        "absences/absenceWebsite/$absenceId"
}

@Composable
fun AbsenceWebsiteRoute(
    absenceId: Int,
    tokenStore: TokenStore,
    studentRepository: StudentRepository,
    onClose: () -> Unit
) {
    val factory = remember(tokenStore, studentRepository) {
        AbsenceWebsiteViewModelFactory(
            tokenStore = tokenStore,
            studentRepository = studentRepository
        )
    }
    val viewModel: AbsenceWebsiteViewModel = viewModel(
        factory = factory
    )
    val state by viewModel.uiState.collectAsState()

    when {
        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        state.errorMessage != null -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(
                    space = 16.dp,
                    alignment = Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(state.errorMessage!!)
                Button(onClick = viewModel::loadSession) {
                    Text("Erneut versuchen")
                }
                Button(onClick = onClose) {
                    Text("Zurück")
                }
            }
        }

        else -> {
            val session = requireNotNull(state.session)

            AbsenceWebsiteScreen(
                absenceId = absenceId,
                userToken = session.token,
                userJson = session.userJson,
                isAdmin = session.isAdmin,
                onClose = onClose
            )
        }
    }
}

data class AbsenceWebsiteSession(
    val token: String,
    val userJson: String,
    val isAdmin: Boolean
)

data class AbsenceWebsiteUiState(
    val isLoading: Boolean = true,
    val session: AbsenceWebsiteSession? = null,
    val errorMessage: String? = null
)

class AbsenceWebsiteViewModel(
    private val tokenStore: TokenStore,
    private val studentRepository: StudentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AbsenceWebsiteUiState())
    val uiState: StateFlow<AbsenceWebsiteUiState> =
        _uiState.asStateFlow()

    init {
        loadSession()
    }

    fun loadSession() {
        viewModelScope.launch {
            _uiState.update {
                AbsenceWebsiteUiState(isLoading = true)
            }

            runCatching {
                createSession()
            }.onSuccess { session ->
                _uiState.update {
                    AbsenceWebsiteUiState(
                        isLoading = false,
                        session = session
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    AbsenceWebsiteUiState(
                        isLoading = false,
                        errorMessage = error.message
                            ?: "Die Website-Sitzung konnte nicht geladen werden."
                    )
                }
            }
        }
    }

    private suspend fun createSession(): AbsenceWebsiteSession {
        val token = tokenStore.getToken()
            ?.removePrefix("Bearer ")
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: throw IllegalStateException(
                "Kein Login-Token vorhanden."
            )

        val claims = decodeJwtPayload(token)
        val userId = claims.optString("id")
            .toIntOrNull()
            ?: throw IllegalStateException(
                "Der Token enthält keine gültige Benutzer-ID."
            )
        val isAdmin = claims.optBooleanClaim("admin")
        val student = studentRepository
            .getStudentData()
            .data
            .student

        val userJson = JSONObject().apply {
            put("createdOn", "")
            put("firstname", student.firstname)
            put("id", userId)
            put("isAdmin", isAdmin)
            put("lastname", student.lastname)
            put("password", "")
            put("student", JSONObject.NULL)
            put("studentId", student.id)
            put("studentReferenceId", "")
            put("teacherReferenceId", JSONObject.NULL)
            put("type", 0)
            put("updatedOn", JSONObject.NULL)
            put("userTeacher", JSONObject.NULL)
            put("username", "")
        }.toString()

        return AbsenceWebsiteSession(
            token = token,
            userJson = userJson,
            isAdmin = isAdmin
        )
    }

    private fun decodeJwtPayload(token: String): JSONObject {
        val parts = token.split('.')
        require(parts.size == 3) {
            "Der gespeicherte Token ist kein gültiger JWT."
        }

        val decodedPayload = runCatching {
            Base64.decode(
                parts[1],
                Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
            ).toString(Charsets.UTF_8)
        }.getOrElse {
            throw IllegalStateException(
                "Der gespeicherte Token konnte nicht gelesen werden.",
                it
            )
        }

        return runCatching {
            JSONObject(decodedPayload)
        }.getOrElse {
            throw IllegalStateException(
                "Die Token-Daten sind ungültig.",
                it
            )
        }
    }

    private fun JSONObject.optBooleanClaim(name: String): Boolean =
        when (val value = opt(name)) {
            is Boolean -> value
            is String -> value.equals(
                other = "true",
                ignoreCase = true
            )

            else -> false
        }
}

class AbsenceWebsiteViewModelFactory(
    private val tokenStore: TokenStore,
    private val studentRepository: StudentRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                AbsenceWebsiteViewModel::class.java
            )
        ) {
            return AbsenceWebsiteViewModel(
                tokenStore = tokenStore,
                studentRepository = studentRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
