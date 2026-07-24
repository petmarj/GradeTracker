package com.example.gradetracker.ui.theme.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ScheduleDayHeaderRow(
    weekStart: LocalDate
) {
    Row {
        // Leere Ecke oberhalb der Zeitspalte
        Box(
            modifier = Modifier
                .width(70.dp)
                .height(70.dp)
                .border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
        )

        repeat(5) { dayIndex ->
            val date = weekStart.plusDays(dayIndex.toLong())

            Column(
                modifier = Modifier
                    .width(130.dp)
                    .height(70.dp)
                    .border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = date.dayOfWeek.getDisplayName(
                        TextStyle.FULL,
                        Locale.GERMAN
                    ).uppercase(),
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = date.format(
                        DateTimeFormatter.ofPattern("d.M.")
                    )
                )
            }
        }
    }
}