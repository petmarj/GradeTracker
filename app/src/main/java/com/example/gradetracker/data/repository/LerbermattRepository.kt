package com.example.gradetracker.data.repository

import com.example.gradetracker.data.remote.LerbermattPublicApi
import com.example.gradetracker.data.remote.model.SchedulerRequest
import com.example.gradetracker.data.remote.model.SchedulerResponse
import com.example.gradetracker.data.remote.model.TimetableLinksResponse
import com.example.gradetracker.model.TimetableLink
import com.example.gradetracker.model.TimetableLinks
import retrofit2.HttpException
import java.io.IOException

class LerbermattRepository(
    private val api: LerbermattPublicApi
) {
    suspend fun getTimetableLinks(): List<TimetableLink> {

        val response = try {
            api.getTimetableLinks(
                request = mapOf("siteId" to 1)
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

        return body.data.timetableLinks
    }
}