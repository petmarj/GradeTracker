package com.example.gradetracker.data.remote


import com.example.gradetracker.data.remote.model.SchedulerRequest
import com.example.gradetracker.data.remote.model.SchedulerResponse
import com.example.gradetracker.data.remote.model.StudentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface LerbermattApi {

    @Headers("Accept: application/json")
    @POST("Scheduler/Get")
    suspend fun getScheduler(
        @Header("Authorization") authorization: String,
        @Body request: SchedulerRequest
    ): Response<SchedulerResponse>

    @Headers("Accept: application/json")
    @GET("Student/GetLoggedInStudent")
    suspend fun getStudent(
        @Header("Authorization") authorization: String,
    ): Response<StudentResponse>
}