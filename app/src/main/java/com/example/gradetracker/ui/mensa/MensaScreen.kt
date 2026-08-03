package com.example.gradetracker.ui.mensa

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gradetracker.model.MensaCategory
import com.example.gradetracker.model.MensaDay
import com.example.gradetracker.model.MensaMeal
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MensaScreen(
    viewModel: MensaViewModel
) {
    val state by viewModel.uiState.collectAsState()
    var selectedDay by remember {
        mutableStateOf<MensaDay?>(null)
    }
    var selectedMeal by remember {
        mutableStateOf<MensaMeal?>(null)
    }

    var showBottomSheet by remember {
        mutableStateOf<Boolean>(false)
    }

    val today = LocalDate.now(
        ZoneId.of("Europe/Zurich")
    )

    LaunchedEffect(
        state.menu?.isoYear,
        state.menu?.isoWeek
    ) {
        val days = state.menu?.days.orEmpty()

        selectedDay = days.firstOrNull { day ->
            day.date == today
        } ?: days.firstOrNull()
    }

    if (state.menu == null) {
        return
    } else {

        val days = state.menu!!.days

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { viewModel.previousWeek() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Vorherige Woche"
                        )
                    }
                    Text(
                        text = "Mensa",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center,
                    )
                    IconButton(onClick = { viewModel.nextWeek() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Nächste Woche"
                        )
                    }
                }
            }

            item {
                DayRow(
                    days = days,
                    selectedDay = selectedDay,
                    onClick = {
                        selectedDay = it
                    }
                )
            }
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val meals = selectedDay?.getAllMeals()

                    meals?.forEach { meal ->
                        MealCard(
                            meal = meal,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                showBottomSheet = true
                                selectedMeal = it
                            }
                        )

                    }

                }
            }
        }
    }

    if (showBottomSheet) {
        MealBottomSheet(
            meal = selectedMeal,
            onDismiss = {
                showBottomSheet = false
            }
        )
    }
}

@Composable
fun DayRow(
    days: List<MensaDay>,
    selectedDay: MensaDay?,
    onClick: (MensaDay) -> Unit
) {
    val zurichZone = ZoneId.of("Europe/Zurich")
    val currentDay: LocalDate = LocalDate.now(zurichZone)


    LazyRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(
            items = days,
            key = { day -> day.date!! }
        ) { day ->
            OutlinedButton(
                onClick = { onClick(day) },
                colors = if (day == selectedDay) {
                    ButtonDefaults.buttonColors()
                } else {
                    ButtonDefaults.outlinedButtonColors()
                },
                border = if (day == selectedDay) {
                    null
                } else {
                    ButtonDefaults.outlinedButtonBorder()
                },
            ) {
                if (day.date == currentDay) {
                    Text("Heute")
                } else {

                    Text(
                        (day.date?.dayOfWeek?.getDisplayName(
                            TextStyle.SHORT,
                            Locale.GERMAN
                        ) ?: "") + " "
                    )
                    Text(
                        day.date?.format(
                            DateTimeFormatter.ofPattern("d.M.")
                        ) ?: ""
                    )
                }
            }
            Spacer(Modifier.width(4.dp))
        }
    }
}

fun MensaCategory.getAllMeals(): List<MensaMeal> {
    return products + categories.flatMap { category ->
        category.getAllMeals()
    }
}

fun MensaDay.getAllMeals(): List<MensaMeal> {
    return categories.flatMap { category ->
        category.getAllMeals()
    }
}
