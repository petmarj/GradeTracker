package com.example.gradetracker.data.remote.model

import com.example.gradetracker.model.Absence
import com.example.gradetracker.model.Exam
import com.example.gradetracker.model.Lesson

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