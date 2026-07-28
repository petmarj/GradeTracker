package com.example.gradetracker.data.repository

import com.example.gradetracker.data.remote.LerbermattApi
import com.example.gradetracker.data.remote.TokenStore
import com.example.gradetracker.data.remote.model.StudentResponse
import retrofit2.HttpException
import java.io.IOException

class StudentRepository(
    private val api: LerbermattApi,
    private val tokenStore: TokenStore
) {
    suspend fun getStudentData(): StudentResponse{
        val token = tokenStore.getToken()
        val authorization = if (token?.startsWith("Bearer ") ?: false) {
            token
        } else {
            "Bearer $token"
        }
        val response = try {
            api.getStudent(
                authorization = authorization
            )
        } catch (exception: IOException) {
            throw IOException(
                "Die Verbindung zur API ist fehlgeschlagen.",
                exception
            )
        }
        if (!response.isSuccessful) {
            throw HttpException(response)
        }

        val body = response.body()
            ?: throw IllegalStateException(
                "Die API hat keine Antwortdaten geliefert."
            )

        if (body.error != "NoError") {
            throw IllegalStateException(
                "API-Fehler: ${body.error}"
            )
        }

        return body
    }
}