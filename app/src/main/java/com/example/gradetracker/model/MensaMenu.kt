package com.example.gradetracker.model

import java.time.LocalDate


typealias AllergenId = Int
typealias SubAllergenId = Int
typealias AdditiveId = Int

data class MensaWeek(
    val storeId: Int,
    val menuId: Int,
    val requestedDate: LocalDate,
    val isoYear: Int,
    val isoWeek: Int,
    val days: List<MensaDay>,
    val allergenCatalog: AllergenCatalog
)

data class MensaDay(
    val date: LocalDate?,
    val weekday: Int?,
    val categories: List<MensaCategory>
)

data class MensaCategory(
    val id: Long?,
    val name: String,
    val description: String?,
    val products: List<MensaMeal>,
    val categories: List<MensaCategory>
)

data class MensaMeal(
    val id: Long?,
    val name: String,
    val description: String?,
    val prices: List<MensaPrice>,
    val allergens: List<MealAllergen>,
    val additives: List<AdditiveId>,
    val labels: List<String>,
    val nutritionFacts: NutritionFacts?,
    val co2Info: MensaCo2Info?
)

data class MensaPrice(
    val label: String?,
    val tag: String?,
    val price: Double?,
    val amountInCents: Long?,
    val currencyCode: String?
)

data class MensaCo2Info(
    val rating: String?,
    val value: Double?
)

data class NutritionFacts(
    val perServing: NutritionValues,
    val per100Grams: NutritionValues
)

data class NutritionValues(
    val caloriesKcal: Int?,
    val energyKJ: Int?,
    val fatGrams: Double?,
    val saturatedFatGrams: Double?,
    val carbohydratesGrams: Double?,
    val sugarGrams: Double?,
    val proteinGrams: Double?,
    val fiberGrams: Double?,
    val saltGrams: Double?
)

data class MealAllergen(
    val allergenId: AllergenId,
    val subAllergenIds: Set<SubAllergenId> = emptySet()
)

data class AllergenCatalog(
    val allergens: Map<AllergenId, AllergenDefinition>
)

data class AllergenDefinition(
    val id: AllergenId,
    val name: String,
    val iconResId: Int,
    val subAllergens: Map<SubAllergenId, SubAllergenDefinition> = emptyMap()
)

data class SubAllergenDefinition(
    val id: SubAllergenId,
    val name: String
)
