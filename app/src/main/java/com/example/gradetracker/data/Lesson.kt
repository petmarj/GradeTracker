package com.example.gradetracker.data

import java.time.DayOfWeek

data class Lesson (
    val lessonId: Int,
    val date: String,
    val dayOfWeek: Int,
    val isCompleted: Boolean,
    val isCancelled: Boolean,
    val subject: LessonSubject,
    val teacher: Teacher,
    val timeslot: TimeSlot,
    val lessonRooms: List<Room>,
    val exams: List<Exam>
    )