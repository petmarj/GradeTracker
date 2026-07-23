package com.example.gradetracker.data

import com.google.gson.annotations.SerializedName
import java.time.DayOfWeek

data class Lesson (
    val id: Int,
    val date: String,
    val dayOfWeek: Int,
    val isCompleted: Boolean,
    val isCancelled: Boolean,
    val subject: LessonSubject,
    val teacher: Teacher,
    val timeslot: TimeSlot,
    val lessonRooms: List<Room>,
    val exams: List<Exam>?
    )