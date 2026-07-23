package com.example.gradetracker.data.API

interface TokenStore {

    suspend fun getToken(): String?

    suspend fun saveToken(token: String)

    suspend fun clearToken()
}