package ch.example.gradetracker.data.remote.model

data class LoginResponse(
    val status: String,
    val error: String,
    val data: LoginData
)

data class LoginData(
    val user: User,
    val isAdmin: Boolean,
    val jwtToken: String,
    val message: String
)

data class User(
    val username: String,
    val firstname: String,
    val lastname: String,
    val studentId: String,
    val id: String
)