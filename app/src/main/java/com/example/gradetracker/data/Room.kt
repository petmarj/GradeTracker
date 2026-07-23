package com.example.gradetracker.data

import com.google.gson.annotations.SerializedName

data class Room (
    val id: Int,
    @SerializedName("text")
    val commandingTeacher: String,
    val namedId: String,
    @SerializedName("longname")
    val subjectName: String
)