package com.example.gradetracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey


data class Student(
    val id: Int,
    val firstname: String,
    val lastname: String,
    val address: Address,
    val nationality: String,
    val majorSubject: String,
    val thirdLanguage: String,
    val artSubject: String,
    val supplementarySubject: String,
    val schoolEmail: String,
    val absencesExcused: Int,
    val absencesUnexcused: Int,
    val delays: Int,
    val halfDaysUsed: Int,
    val dispensations: Int,
    val absencesOpen: Int,
    val classStr: String,
    val mint: Boolean,
    val musicalInstrument: String,
    val additionalLanguage: String,
    val birthdate: String
)


data class Address(
    val addressLine1: String,
    val addressLine2: String,
    val city: String,
    val zipCode: String
)