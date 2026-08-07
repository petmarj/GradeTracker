package ch.example.gradetracker.model

import com.google.gson.annotations.SerializedName

data class Absence(
    val id: Int,
    val date: String,
    val commentStudent: String,
    val commentParent: String,
    val commentTeacher: String,
    val commentIntern: String,
    val state: Int,
    val type: Int,
    val parentConfirmationState: Int,
    val lesson: Lesson,
    @SerializedName("timeslot")
    val timeSlot: TimeSlot,
    val parentConfirmationGuid: String
)