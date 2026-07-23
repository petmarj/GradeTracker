package com.example.gradetracker.repo


import com.example.gradetracker.data.API.SchedulerApi
import com.example.gradetracker.data.API.SchedulerRequest
import com.example.gradetracker.data.API.SchedulerResponse
import com.example.gradetracker.data.API.TokenStore
import com.example.gradetracker.data.database.dao.StudentDao
import retrofit2.HttpException
import java.io.IOException

class SchedulerRepository(
    private val api: SchedulerApi,
    private val studentDao: StudentDao,
    private val tokenStore: TokenStore
) {

    suspend fun getSchedule(
        token: String,
        from: String,
        to: String
    ): SchedulerResponse {
        val request = SchedulerRequest(
            from = from,
            to = to
        )

        val authorization = if (token.startsWith("Bearer ")) {
            token
        } else {
            "Bearer $token"
        }

        val response = try {
            api.getScheduler(
                authorization = authorization,
                request = request
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