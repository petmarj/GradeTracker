package ch.example.gradetracker.ui.mensa

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gradetracker.model.MensaMeal
import com.example.gradetracker.model.MensaPrice
import java.util.Locale

@Composable
fun MealCard(
    meal: MensaMeal,
    modifier: Modifier,
    onClick: (MensaMeal) -> Unit
) {

    ElevatedCard(
        modifier = modifier.combinedClickable(
            onClick = { onClick(meal) }
        ),
    ) {
        Column(
            Modifier.padding(16.dp)
        ) {
            Text(
                text = meal.name,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            meal.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "CHF " + String.format(
                    Locale.US,
                    "%.2f",
                    getFormattedPrices(meal.prices).getValue("SCHU")
                ),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

fun getFormattedPrices(
    mensaPrices: List<MensaPrice>
): Map<String, Double> {
    return mensaPrices.mapNotNull { price ->
        val label = price.label
            ?: price.tag
            ?: return@mapNotNull null

        val formattedPrice = price.amountInCents
            ?.div(100.0)
            ?: price.price
            ?: return@mapNotNull null

        label to formattedPrice
    }.toMap()
}