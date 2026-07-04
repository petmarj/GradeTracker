package com.example.gradetracker.data

import java.util.UUID

data class SchoolYear (
    val id: String = UUID.randomUUID().toString(),
    val name: String
)