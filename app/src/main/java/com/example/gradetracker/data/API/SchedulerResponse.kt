package com.example.gradetracker.data.API

import com.example.gradetracker.data.Absence
import com.example.gradetracker.data.Exam
import com.example.gradetracker.data.Lesson

data class SchedulerResponse(
    val data: SchedulerData,
    val status: String?,
    val error: String
)

data class SchedulerData(
    val lessons: List<Lesson>,
    val absences: List<Absence>,
    val exams: List<Exam>
)