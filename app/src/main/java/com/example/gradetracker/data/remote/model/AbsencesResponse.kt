package com.example.gradetracker.data.remote.model

import com.example.gradetracker.model.Absence

data class AbsencesResponse(
    val data: AbsenceData,
    val status: String?,
    val error: String
)

data class AbsenceData(
    val absences: List<Absence>
)
