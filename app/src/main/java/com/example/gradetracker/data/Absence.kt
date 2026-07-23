package com.example.gradetracker.data

data class Absence (
    val id: Int,
    val date: String,
    val commentStudent: String,
    val commentParent: String,
    val commentTeacher: String,
    val commentIntern: String,
    val state: Int,
    val type: Int,
    val parentConfirmationState: Int,
    val timeslot: TimeSlot
)