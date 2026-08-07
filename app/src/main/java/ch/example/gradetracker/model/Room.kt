package ch.example.gradetracker.model

import com.google.gson.annotations.SerializedName

data class LessonRoom(
    val lessonId: Int,
    val roomId: Int,
    val room: Room
)

data class Room(
    val id: Int,
    @SerializedName("text")
    val commandingTeacher: String?,
    val namedId: String,
    @SerializedName("longname")
    val subjectName: String
)