package com.example.gradetracker.ui.theme.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gradetracker.data.ScheduleTimeSlot

@Composable
fun TimeCell(
    timeSlot: ScheduleTimeSlot
) {
    Column(
        modifier = Modifier
            .width(70.dp)
            .height(55.dp)
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(formatTime(timeSlot.startTime))
        Text(formatTime(timeSlot.endTime))
    }
}

private fun formatTime(time: Int): String {
    val hours = time / 100
    val minutes = time % 100

    return "%02d:%02d".format(hours, minutes)
}