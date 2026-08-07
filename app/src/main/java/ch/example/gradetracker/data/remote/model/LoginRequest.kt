package ch.example.gradetracker.data.remote.model

data class LoginRequest(
    val password: String,
    val username: String
)