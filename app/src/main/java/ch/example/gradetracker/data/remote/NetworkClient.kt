package ch.example.gradetracker.data.remote


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkClient {

    private const val ABSENCE_BASE_URL =
        "https://absenzen.lerbermatt.ch/api/v1/"

    private const val PUBLIC_BASE_URL =
        "https://lerbermatt.ch/"

    private const val SV_GROUP_BASE_URL =
        "https://apps-live-eu.qnips.com/cons/api/"

    val lerbermattApi: LerbermattApi by lazy {
        Retrofit.Builder()
            .baseUrl(ABSENCE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LerbermattApi::class.java)
    }

    val publicApi: LerbermattPublicApi by lazy {
        Retrofit.Builder()
            .baseUrl(PUBLIC_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LerbermattPublicApi::class.java)
    }

    val svGroupApi: SVGroupAPI by lazy {
        Retrofit.Builder()
            .baseUrl(SV_GROUP_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SVGroupAPI::class.java)
    }
}