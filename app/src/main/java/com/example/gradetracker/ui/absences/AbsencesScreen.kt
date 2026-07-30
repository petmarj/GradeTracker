package com.example.gradetracker.ui.absences

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.gradetracker.model.Absence
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private enum class AbsenceTab(
    val title: String,
    val absenceType: Int
) {
    UNPLANNED("Ungeplant", 0),
    PLANNED("Geplant", 1),
    DELAYS("Verspätungen", 2),
    DISPENSATIONS("Dispensationen", 3)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbsencesScreen(
    viewModel: AbsencesViewModel,
    onOpenWebsite: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    val selectedTab = AbsenceTab.entries[selectedTabIndex]
    var showAbsenceCard by remember { mutableStateOf(false) }
    var showingAbsence by remember { mutableStateOf<Absence?>(null) }

    val filteredAbsences = state.absences
        .orEmpty()
        .filter { absence ->
            absence.type == selectedTab.absenceType
        }
        .sortedWith(
            compareBy<Absence> { absence -> absence.state }
                .thenByDescending { absence ->
                    if (absence.id in state.unreadAbsenceIds) 1 else 0
                }
        )
   Column(
        modifier = Modifier.padding(8.dp).fillMaxSize(),
    ) {
       PullToRefreshBox(
           modifier = Modifier.fillMaxWidth().weight(1f),
           contentAlignment = Alignment.Center,
           onRefresh = {viewModel.getData(true)},
           isRefreshing = state.isRefreshing
       ) {
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Lädt...")
                }
            }

            state.errorMessage != null -> {

                Box(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.errorMessage!!)
                }

            }

            else -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    PrimaryScrollableTabRow(
                        selectedTabIndex = selectedTabIndex,
                        edgePadding = 8.dp
                    ) {
                        AbsenceTab.entries.forEachIndexed { index, tab ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = {
                                    if (selectedTabIndex != index) {
                                        val previousTab =
                                            AbsenceTab.entries[selectedTabIndex]
                                        viewModel.markAbsencesAsRead(
                                            previousTab.absenceType
                                        )
                                        selectedTabIndex = index
                                    }
                                },
                                text = {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = tab.title,
                                            maxLines = 1
                                        )

                                        val unreadCount = state.absences
                                            .orEmpty()
                                            .count { absence ->
                                                absence.type == tab.absenceType &&
                                                        absence.id in state.unreadAbsenceIds
                                            }

                                        if (unreadCount > 0) {
                                            Badge {
                                                Text(unreadCount.toString())
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                    if (filteredAbsences.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Keine Einträge"
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = filteredAbsences,
                                key = { absence -> absence.id }
                            ) { absence ->
                                ElevatedCard(
                                    modifier = Modifier.fillMaxWidth()
                                        .combinedClickable(
                                            onClick = {
                                                showAbsenceCard = true
                                                showingAbsence = absence
                                                viewModel.markAbsenceAsRead(absence.id)
                                            }
                                        ),
                                ) {
                                    Box {
                                        Column(
                                            modifier = Modifier.padding(
                                                start = 16.dp,
                                                top = 16.dp,
                                                end = 32.dp,
                                                bottom = 16.dp
                                            ),
                                            verticalArrangement =
                                                Arrangement.spacedBy(4.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = absence.lesson.subject.name ?: "",
                                                    style = MaterialTheme.typography.titleMedium
                                                )

                                                if (absence.id in state.unreadAbsenceIds) {
                                                    Badge {
                                                        Text("Neu")
                                                    }
                                                }
                                            }

                                            Text(
                                                LocalDateTime.parse(absence.date).dayOfWeek.getDisplayName(
                                                    TextStyle.FULL, Locale.GERMAN
                                                )
                                                        + ", " +
                                                        LocalDateTime
                                                            .parse(absence.date)
                                                            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))

                                            )
                                            Text(
                                                formatTime(absence.timeSlot.startTime) + " - " + formatTime(
                                                    absence.timeSlot.endTime
                                                )
                                            )
                                        }

                                        AbsenceStateStripe(
                                            state = absence.state,
                                            modifier = Modifier.matchParentSize()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        }

       if (showAbsenceCard) {
           ModalBottomSheet(
               onDismissRequest = {
                   showAbsenceCard = false
                   showingAbsence = null
               }
           ) {
               Box(modifier = Modifier.fillMaxSize()){
                   AbsenceInformationCard(
                       absence = showingAbsence,
                       onOpenWebsite = { absenceId ->
                           showAbsenceCard = false
                           showingAbsence = null
                           onOpenWebsite(absenceId)
                       }
                   )
               }
           }
       }
    }
}
private fun formatTime(time: Int): String {
    val hours = time / 100
    val minutes = time % 100

    return "%02d:%02d".format(hours, minutes)
}

@Composable
private fun AbsenceStateStripe(
    state: Int,
    modifier: Modifier = Modifier
) {
    val color = when (state) {
        0 -> Color(0xFFFF0000)
        1 -> Color(0xFFFFFF00)
        2 -> Color(0xFF00F000)
        else -> Color.Transparent
    }

    Canvas(modifier = modifier) {
        val stripeWidth = 20.dp.toPx()

        drawRect(
            color = color,
            size = Size(
                width = stripeWidth,
                height = size.height
            ),
            topLeft = Offset(
                x = size.width - stripeWidth,
                y = 0f
            )
        )
    }
}
