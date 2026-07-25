package com.example.gradetracker.ui.theme.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ScheduleHeader(
            weekStart = state.weekStart,
            onPreviousWeek = viewModel::previousWeek,
            onNextWeek = viewModel::nextWeek,
        )

        if (state.holiday != null) {

            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Es sind gerade ${state.holiday!!.displayName}",
                    textAlign = TextAlign.Center
                )
            }


        } else {


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
    }
}