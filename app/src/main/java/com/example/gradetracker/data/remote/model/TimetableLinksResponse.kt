package com.example.gradetracker.data.remote.model

import com.example.gradetracker.model.TimetableLink
import com.google.gson.annotations.SerializedName

data class TimetableLinksResponse (
    val data: TimetableLinksData,
)

data class TimetableLinksData(
    @SerializedName("timetables")
    val timetableLinks: List<TimetableLink>
)
