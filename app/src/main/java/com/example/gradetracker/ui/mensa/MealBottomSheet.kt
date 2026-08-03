package com.example.gradetracker.ui.mensa

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gradetracker.model.MensaMeal
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealBottomSheet(
    meal: MensaMeal?,
    onDismiss: () -> Unit,

    ) {
    if (meal == null) return

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(
            topStart = 32.dp,
            topEnd = 32.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 32.dp
                )

        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Column(
                        Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = meal.name,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        meal.description?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        }
                        Text(
                            text = "",
                            style = MaterialTheme.typography.bodyMedium
                        )

                    }
                }
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Column(
                        Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Ratings",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        meal.description?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        val prices = getFormattedPrices(meal.prices)

                        val pricesText = listOf(
                            "EXT",
                            "LEHR",
                            "SCHU"
                        ).joinToString(separator = " / ") { name ->
                            val formattedPrice = prices[name]?.let { price ->
                                String.format(Locale.US, "%.2f", price)
                            } ?: "--.--"

                            "$name CHF $formattedPrice"
                        }
                        Text(
                            text = pricesText,
                            style = MaterialTheme.typography.bodyMedium
                        )

                    }
                }
            }
        }
    }
}

