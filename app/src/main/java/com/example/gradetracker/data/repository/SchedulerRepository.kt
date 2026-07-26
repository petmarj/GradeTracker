package com.example.gradetracker.data.repository


import com.example.gradetracker.data.remote.LerbermattApi
import com.example.gradetracker.data.remote.model.SchedulerRequest
import com.example.gradetracker.data.remote.model.SchedulerResponse
import com.example.gradetracker.data.remote.TokenStore
import retrofit2.HttpException
import java.io.IOException

class SchedulerRepository(
    private val api: LerbermattApi,
    //private val studentDao: StudentDao,
    private val tokenStore: TokenStore

) {


    suspend fun getSchedule(
        from: String,
        to: String
    ): SchedulerResponse {
        val request = SchedulerRequest(
            from = from,
            to = to
        )
        val token = tokenStore.getToken()

        val authorization = if (token?.startsWith("Bearer ") ?: false) {
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