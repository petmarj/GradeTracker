package com.example.gradetracker.data

import com.google.gson.annotations.SerializedName

data class Teacher (
    val id: Int,
    @SerializedName("forename")
    val firstname: String,
    @SerializedName("surname")
    val lastname: String,
    val namedId: String
)
