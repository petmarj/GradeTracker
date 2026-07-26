package com.example.gradetracker.model

import com.google.gson.annotations.SerializedName

data class TimeSlot (
    val id: Int,
    @SerializedName("starttime")
    val startTime: Int,
    @SerializedName("endtime")
    val endTime: Int,
    val slot: Int
)