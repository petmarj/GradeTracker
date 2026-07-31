package com.example.gradetracker.data.remote.model

import com.example.gradetracker.model.TimetableLink

data class TimetableLinksResponse (
    val data: TimetableLinksData,
    val status: String?,
    val error: String
)

data class TimetableLinksData(
    val timetableLinks: List<TimetableLink>
)
