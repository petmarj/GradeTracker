package com.example.gradetracker.data.API

interface TokenStore {

    suspend fun getToken(): String?{return "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjM0NTEiLCJ0eXBlIjoiU3R1ZGVudCIsImFkbWluIjoiRmFsc2UiLCJpbXBlcnNvbmF0ZSI6IiIsIm5iZiI6MTc4NDY0MDM4MiwiZXhwIjoxODE2MTc2MzgyLCJpc3MiOiJBYnNlbmNlU3lzdGVtQXBpIiwiYXVkIjoiaHR0cHM6Ly9hYnNlbnplbi5sZXJiZXJtYXR0LmNoIn0.VYJDiyxHihZ7A9oWqsnTLzJceNiB2B9sFFdQGNiZ8QKs5TjdxJtnFgnlzD92GGKKp-UyMIecPI0EpJE9Wk1VHQ"}

    suspend fun saveToken(token: String){TODO()}

    suspend fun clearToken(){TODO()}
}