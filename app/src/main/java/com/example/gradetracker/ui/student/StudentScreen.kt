package com.example.gradetracker.ui.student

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StudentScreen(
    viewModel: StudentViewModel,
) {
    val state by viewModel.uiState.collectAsState()
    LazyColumn(
        modifier = Modifier.padding(8.dp),
    ) {
        when {
            state.isLoading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            state.errorMessage != null -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(state.errorMessage!!)
                    }
                }
            }

            else -> {
                item {
                    StudentHeadCard(
                        state = state
                    )
                }

                item {
                    CoursesCard(
                        state = state
                    )
                }

                item {
                    AbsencesCard(
                        state = state
                    )
                }

                item {
                    PersonalinformationCard(
                        state = state
                    )
                }
            }
        }
    }
}