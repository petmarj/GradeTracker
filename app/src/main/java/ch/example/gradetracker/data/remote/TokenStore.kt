package ch.example.gradetracker.data.remote

import android.content.Context
import androidx.core.content.edit

interface TokenStore {

    suspend fun getToken(): String?

    suspend fun saveToken(token: String)

    suspend fun clearToken()
}


class SharedPreferencesTokenStore(
    context: Context
) : TokenStore {

    private val preferences = context.applicationContext
        .getSharedPreferences(
            "token_store",
            Context.MODE_PRIVATE
        )

    override suspend fun getToken(): String? {
        return preferences.getString("bearer_token", null)
    }

    override suspend fun saveToken(token: String) {
        preferences.edit {
            putString(
                "bearer_token",
                token.removePrefix("Bearer ").trim()
            )
        }
    }

    override suspend fun clearToken() {
        preferences.edit {
            remove("bearer_token")
        }
    }
}