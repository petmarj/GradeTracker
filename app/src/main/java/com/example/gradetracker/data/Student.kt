package com.example.gradetracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.intellij.lang.annotations.Language


@Entity
data class Student(
    @PrimaryKey
    val id: Int,
    val firstname: String,
    val lastname: String,
    val address: String,
    val tel: String,
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
    val classStr: String
)