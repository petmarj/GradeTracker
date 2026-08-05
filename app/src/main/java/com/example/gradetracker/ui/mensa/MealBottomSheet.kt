package com.example.gradetracker.ui.mensa

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gradetracker.model.AllergenCatalog
import com.example.gradetracker.model.MensaMeal
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealBottomSheet(
    meal: MensaMeal?,
    allergenCatalog: AllergenCatalog,
    onDismiss: () -> Unit
) {
    if (meal == null) return

    val allergenRows = meal.allergens.mapNotNull { mealAllergen ->
        val allergen =
            allergenCatalog.allergens[mealAllergen.allergenId]
                ?: return@mapNotNull null

        val subAllergenNames =
            mealAllergen.subAllergenIds.mapNotNull { id ->
                allergen.subAllergens[id]?.name
            }

        val text = if (subAllergenNames.isEmpty()) {
            allergen.name
        } else {
            "${allergen.name}: ${subAllergenNames.joinToString()}"
        }

        allergen.iconResId to text
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(
            topStart = 32.dp,
            topEnd = 32.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 32.dp
                ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = meal.name,
                        style = MaterialTheme.typography.titleLarge
                    )

                    meal.description?.let { description ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
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
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = pricesText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (allergenRows.isNotEmpty()) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Allergene",
                            style = MaterialTheme.typography.titleLarge
                        )

                        allergenRows.forEach { (iconResId, allergenText) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Image(
                                    painter = painterResource(
                                        id = iconResId
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp),
                                    contentScale = ContentScale.Fit
                                )
                                Text(
                                    text = allergenText,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
