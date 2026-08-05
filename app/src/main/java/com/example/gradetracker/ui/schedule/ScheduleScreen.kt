package com.example.gradetracker.ui.schedule

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.gradetracker.model.Lesson


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: SchedulerViewModel
) {

    val state by viewModel.uiState.collectAsState()

    val schedulerData = state.schedule?.data

    val lessons = schedulerData?.lessons.orEmpty()
    val absences = schedulerData?.absences.orEmpty()
    val exams = schedulerData?.exams.orEmpty()
    val lessonsByDay = state.schedule
        ?.data
        ?.lessons
        .orEmpty()
        .groupBy { lesson -> lesson.dayOfWeek }

    var showBottomSheet by remember { mutableStateOf(false) }
    var showingLesson by remember { mutableStateOf<Lesson?>(null) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ScheduleHeader(
            weekStart = state.weekStart,
            onPreviousWeek = viewModel::previousWeek,
            onNextWeek = viewModel::nextWeek,
        )

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when {
                state.errorMessage != null -> {
                    MessageBox(
                        text = state.errorMessage!!
                    )
                }

                state.holiday != null -> {
                    MessageBox(
                        text = state.holiday!!.displayName
                    )
                }

                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                else -> {
                    ScheduleGrid(
                        weekStart = state.weekStart,
                        lessonsByDay = lessonsByDay,
                        absences = absences,
                        exams = exams,
                        onClick = {
                            showBottomSheet = true
                            showingLesson = it
                        }
                    )
                }
            }

        }
    }
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                showingLesson = null
            }
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                LessonSheet(
                    lesson = showingLesson,
                    absences = absences,
                    exams = exams
                )
            }
        }
    }
}

@Composable
private fun MessageBox(
    text: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center
        )
    }
}