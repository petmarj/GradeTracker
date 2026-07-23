package com.example.gradetracker.data.API


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkClient {

    private const val BASE_URL =
        "https://absenzen.lerbermatt.ch/api/v1/"

    val lerbermattApi: LerbermattApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LerbermattApi::class.java)
    }
}