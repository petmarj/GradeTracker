package com.example.gradetracker.data.remote

import com.example.gradetracker.data.remote.model.TimetableLinksResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface LerbermattPublicApi {

    @Headers("Accept: application/json")
    @POST("queries/timetables")
    suspend fun getTimetableLinks(
        @Body request: Map<String, Int>
    ): Response<TimetableLinksResponse>
}