package com.example.gradetracker.data.remote

import com.example.gradetracker.data.remote.model.FirebaseSignInRequest
import com.example.gradetracker.data.remote.model.FirebaseSignInResponse
import com.example.gradetracker.data.remote.model.NewIdentityResponse
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

interface SVGroupAPI {

    @Headers(
        "Accept: application/json",
        "Accept-Language: de-CH",
        "App-Brand: svgroupch",
        "Response-Version: v3",
        "Authorization: 00981c0f9391630f993c9c2ecdb8b46f67"
    )
    @GET("NewIdentity")
    suspend fun createAnonymousIdentity(): Response<NewIdentityResponse>

    @POST
    suspend fun signInWithCustomToken(
        @Url url: String,
        @Body request: FirebaseSignInRequest
    ): Response<FirebaseSignInResponse>

    @GET
    suspend fun getFirestoreDocument(
        @Url url: String,
        @Header("Authorization") authorization: String
    ): Response<JsonObject>
}