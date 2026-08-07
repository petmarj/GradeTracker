package ch.example.gradetracker.data.remote.model

import com.example.gradetracker.model.Student


data class StudentResponse(
    val data: StudentData,
    val status: String?,
    val error: String
)

data class StudentData(
    val student: Student
)

