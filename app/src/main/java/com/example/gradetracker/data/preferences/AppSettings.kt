package com.example.gradetracker.data.preferences

import com.example.gradetracker.model.GradeColorMode
import com.example.gradetracker.model.GradeSort
import com.example.gradetracker.model.SubjectSort
import com.example.gradetracker.ui.theme.ThemeMode

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColors: Boolean = true,
    val subjectSort: SubjectSort = SubjectSort.NEWEST,
    val gradeSort: GradeSort = GradeSort.NEWEST,
    val gradeColorMode: GradeColorMode = GradeColorMode.NORMAL
)
