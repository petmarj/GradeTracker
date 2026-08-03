package com.example.gradetracker.data.remote.model

import com.google.gson.annotations.SerializedName

data class NewIdentityResponse(
    @SerializedName("UserId")
    val userId: Long,

    @SerializedName("FirebaseCustomToken")
    val firebaseCustomToken: String,

    @SerializedName("FirebaseRegion")
    val firebaseRegion: String,

    @SerializedName("Environment")
    val environment: String
)

data class FirebaseSignInRequest(
    val token: String,
    val returnSecureToken: Boolean = true
)

data class FirebaseSignInResponse(
    val idToken: String,
    val refreshToken: String,
    val expiresIn: String
)