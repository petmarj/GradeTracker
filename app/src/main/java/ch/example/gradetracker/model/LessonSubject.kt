package ch.example.gradetracker.model

import com.google.gson.annotations.SerializedName

data class LessonSubject(
    val id: Int,
    @SerializedName("longname")
    val name: String
)