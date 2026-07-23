package com.example.gradetracker.data.API


import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface SchedulerApi {

    @Headers("Accept: application/json")
    @POST("Scheduler/Get")
    suspend fun getScheduler(
        @Header("Authorization") authorization: String,
        @Body request: SchedulerRequest
    ): Response<SchedulerResponse>
}