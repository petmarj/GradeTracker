package com.example.gradetracker.ui.theme.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.gradetracker.data.API.NetworkClient
import com.example.gradetracker.data.API.TokenStore
import com.example.gradetracker.ui.theme.components.SchedulerViewModel
import com.example.gradetracker.data.Lesson
import com.example.gradetracker.data.SchedulerViewModelFactory
import com.example.gradetracker.repo.SchedulerRepository
import com.example.gradetracker.ui.theme.components.ScheduleGrid
import com.example.gradetracker.ui.theme.components.ScheduleHeader
import kotlin.math.abs


@Composable
fun ScheduleScreen(
    viewModel: SchedulerViewModel
) {
    val tokenStore  = object : TokenStore{}
    val state by viewModel.uiState.collectAsState()

    val schedulerData = state.schedule?.data

    val lessons = schedulerData?.lessons.orEmpty()
    val absences = schedulerData?.absences.orEmpty()
    val exams = schedulerData?.exams.orEmpty()


    ScheduleHeader(
        weekStart = state.weekStart,
        onPreviousWeek = viewModel::previousWeek,
        onNextWeek = viewModel::nextWeek,
        onRefresh = viewModel::refresh
    )

    if(state.holiday != null){
        Text(
            text = "Es sind gerade ${state.holiday}"
        )
    }
    val lessonsByDay = state.schedule
        ?.data
        ?.lessons
        .orEmpty()
        .groupBy { lesson -> lesson.dayOfWeek }

    ScheduleGrid(
        weekStart = state.weekStart,
        lessonsByDay = lessonsByDay,
        absences = absences,
        exams = exams
    )
}