package com.example.gradetracker.data.repository

import com.example.gradetracker.data.remote.SVGroupAPI
import com.example.gradetracker.data.remote.model.FirebaseSignInRequest
import com.example.gradetracker.model.MensaCategory
import com.example.gradetracker.model.MensaCo2Info
import com.example.gradetracker.model.MensaDay
import com.example.gradetracker.model.MensaMeal
import com.example.gradetracker.model.MensaPrice
import com.example.gradetracker.model.MensaWeek
import com.example.gradetracker.model.NutritionFacts
import com.example.gradetracker.model.NutritionValues
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.IsoFields

class MensaRepository(
    private val api: SVGroupAPI
) {
    private val authenticationMutex = Mutex()
    private var cachedAuthentication: Authentication? = null

    suspend fun getMenu(
        date: LocalDate = LocalDate.now()
    ): MensaWeek {
        return try {
            val authentication = getAuthentication()

            val isoYear = date.get(IsoFields.WEEK_BASED_YEAR)
            val isoWeek = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)

            val response = api.getFirestoreDocument(
                url = createMenuUrl(
                    environment = authentication.environment,
                    isoYear = isoYear,
                    isoWeek = isoWeek
                ),
                authorization = "Bearer ${authentication.idToken}"
            )

            val document = response.requireBody()
            val fields = decodeFirestoreDocument(document)

            val days = fields.mapList("Days")
                .map(::mapDay)
                .filter { day -> day.categories.isNotEmpty() }
                .sortedBy { day -> day.date }

            MensaWeek(
                storeId = LERBERMATT_STORE_ID,
                menuId = LERBERMATT_MENU_ID,
                requestedDate = date,
                isoYear = isoYear,
                isoWeek = isoWeek,
                days = days
            )
        } catch (exception: IOException) {
            throw IOException(
                "Die Verbindung zur SV-Group-API ist fehlgeschlagen.",
                exception
            )
        }
    }

    private suspend fun getAuthentication(): Authentication {
        return authenticationMutex.withLock {
            val now = Instant.now().epochSecond

            val cached = cachedAuthentication
            if (
                cached != null &&
                now < cached.expiresAtEpochSeconds - TOKEN_EXPIRY_MARGIN_SECONDS
            ) {
                return@withLock cached
            }

            val identity = api.createAnonymousIdentity().requireBody()

            val signIn = api.signInWithCustomToken(
                url = FIREBASE_SIGN_IN_URL,
                request = FirebaseSignInRequest(
                    token = identity.firebaseCustomToken
                )
            ).requireBody()

            val expiresInSeconds =
                signIn.expiresIn.toLongOrNull() ?: DEFAULT_TOKEN_LIFETIME_SECONDS

            Authentication(
                idToken = signIn.idToken,
                environment = identity.environment,
                expiresAtEpochSeconds = now + expiresInSeconds
            ).also { authentication ->
                cachedAuthentication = authentication
            }
        }
    }

    private fun createMenuUrl(
        environment: String,
        isoYear: Int,
        isoWeek: Int
    ): String {
        return "$FIRESTORE_DOCUMENTS_URL/" +
                "$environment/$LANGUAGE/Menus/$LERBERMATT_MENU_ID/" +
                "Years/$isoYear/Weeks/$isoWeek"
    }

    private fun mapDay(fields: Map<String, Any?>): MensaDay {
        return MensaDay(
            date = parseDate(fields.text("Date")),
            weekday = fields.number("WeekDay")?.toInt(),
            categories = fields.mapList("Categories")
                .map(::mapCategory)
        )
    }

    private fun mapCategory(
        fields: Map<String, Any?>
    ): MensaCategory {
        return MensaCategory(
            id = fields.number("Id")?.toLong(),
            name = fields.text("Name").orEmpty(),
            description = fields.text("Description"),
            products = fields.mapList("Products")
                .map(::mapMeal),
            categories = fields.mapList("Categories")
                .map(::mapCategory)
        )
    }

    private fun mapMeal(
        fields: Map<String, Any?>
    ): MensaMeal {
        val allergens = listOf(
            "Allergens",
            "SubAllergens0",
            "SubAllergens7"
        ).flatMap { key ->
            collectIds(fields[key])
        }.distinct()

        val additives = collectIds(
            fields["Additives"]
        ).distinct()

        val labels = (
                collectNames(fields["CustomTags"]) +
                        collectNames(fields["LabelGroups"]) +
                        collectNames(fields["Tags"])
                )
            .filter(String::isNotBlank)
            .distinct()

        val co2Fields = fields["CO2Info"].asStringMap()

        return MensaMeal(
            id = fields.number("Id")?.toLong(),
            name = fields.firstText(
                "Name",
                "Title"
            ).orEmpty(),
            description = fields.firstText(
                "Teaser"
            ),
            prices = fields.mapList("Prices")
                .map(::mapPrice),
            allergens = allergens,
            additives = additives,
            labels = labels,
            co2Info = co2Fields?.let {
                MensaCo2Info(
                    rating = it.text("Rating"),
                    value = it.number("Value")?.toDouble()
                )
            },
            nutritionFacts = mapNutritionFacts(
                fields["NutritionFacts"].asStringMap()
            )
        )
    }

    private fun mapNutritionFacts(
        fields: Map<String, Any?>?
    ): NutritionFacts? {
        if (fields == null) {
            return null
        }

        return NutritionFacts(
            perServing = NutritionValues(
                caloriesKcal = fields.number("KCalPerServing")?.toInt(),
                energyKJ = fields.number("KJPerServing")?.toInt(),
                fatGrams = fields.number("FatsPerServing")?.toDouble(),
                saturatedFatGrams =
                    fields.number("SatsPerServing")?.toDouble(),
                carbohydratesGrams =
                    fields.number("CarbsPerServing")?.toDouble(),
                sugarGrams =
                    fields.number("SugarPerServing")?.toDouble(),
                proteinGrams =
                    fields.number("ProteinsPerServing")?.toDouble(),
                fiberGrams =
                    fields.number("RoughagePerServing")?.toDouble(),
                saltGrams =
                    fields.number("SaltPerServing")?.toDouble()
            ),
            per100Grams = NutritionValues(
                caloriesKcal = fields.number("KCalPer100")?.toInt(),
                energyKJ = fields.number("KJPer100")?.toInt(),
                fatGrams = fields.number("FatsPer100")?.toDouble(),
                saturatedFatGrams =
                    fields.number("SatsPer100")?.toDouble(),
                carbohydratesGrams =
                    fields.number("CarbsPer100")?.toDouble(),
                sugarGrams =
                    fields.number("SugarPer100")?.toDouble(),
                proteinGrams =
                    fields.number("ProteinsPer100")?.toDouble(),
                fiberGrams =
                    fields.number("RoughagePer100")?.toDouble(),
                saltGrams =
                    fields.number("SaltPer100")?.toDouble()
            )
        )
    }

    private fun collectIds(value: Any?): List<Long> {
        return when (value) {
            is Number -> listOf(value.toLong())

            is String ->
                value.toLongOrNull()?.let(::listOf) ?: emptyList()

            is List<*> ->
                value.flatMap(::collectIds)

            is Map<*, *> -> {
                val directId = collectIds(value["Id"])

                val nestedIds = listOf(
                    "Items",
                    "Values",
                    "Allergens",
                    "Additives"
                ).flatMap { key ->
                    collectIds(value[key])
                }

                directId + nestedIds
            }

            else -> emptyList()
        }
    }

    private fun mapPrice(
        fields: Map<String, Any?>
    ): MensaPrice {
        val localizablePrice =
            fields["LocalizablePrice"].asStringMap()

        return MensaPrice(
            label = fields.text("Label"),
            tag = fields.text("Tag"),
            price = fields.number("Price")?.toDouble(),
            amountInCents =
                localizablePrice?.number("Amount")?.toLong(),
            currencyCode =
                localizablePrice?.text("CurrencyCode")
        )
    }

    private fun parseDate(value: String?): LocalDate? {
        if (value == null || value.length < 10) {
            return null
        }

        return runCatching {
            LocalDate.parse(value.take(10))
        }.getOrNull()
    }

    /*
     * Sucht rekursiv nach Namen innerhalb von Allergenen,
     * Zusatzstoffen, CustomTags und LabelGroups.
     */
    private fun collectNames(value: Any?): List<String> {
        return when (value) {
            is String -> listOf(value)

            is List<*> -> value.flatMap(::collectNames)

            is Map<*, *> -> {
                val map = value.entries
                    .mapNotNull { (key, itemValue) ->
                        val stringKey = key as? String
                            ?: return@mapNotNull null

                        stringKey to itemValue
                    }
                    .toMap()

                val directNames = listOf(
                    "Name",
                    "Title",
                    "ShortName",
                    "Label",
                    "Tag",
                    "Code"
                ).mapNotNull { key ->
                    map[key] as? String
                }

                val nestedNames = listOf(
                    "Labels",
                    "Tags",
                    "Items",
                    "Values",
                    "CustomTags",
                    "Allergens",
                    "Additives"
                ).flatMap { key ->
                    collectNames(map[key])
                }

                (directNames + nestedNames).distinct()
            }

            else -> emptyList()
        }
    }

    /*
     * Wandelt das typisierte Firestore-REST-Format in
     * normale Kotlin-Werte um.
     */
    private fun decodeFirestoreDocument(
        document: JsonObject
    ): Map<String, Any?> {
        val fields = document.get("fields")

        if (fields == null || !fields.isJsonObject) {
            return emptyMap()
        }

        return decodeFirestoreFields(fields.asJsonObject)
    }

    private fun decodeFirestoreFields(
        fields: JsonObject
    ): Map<String, Any?> {
        return fields.entrySet().associate { (name, value) ->
            name to decodeFirestoreValue(value)
        }
    }

    private fun decodeFirestoreValue(
        element: JsonElement
    ): Any? {
        if (!element.isJsonObject) {
            return null
        }

        val value = element.asJsonObject

        return when {
            value.has("nullValue") -> null

            value.has("booleanValue") ->
                value.get("booleanValue").asBoolean

            value.has("integerValue") ->
                value.get("integerValue").asString.toIntOrNull()

            value.has("doubleValue") ->
                value.get("doubleValue").asDouble

            value.has("timestampValue") ->
                value.get("timestampValue").asString

            value.has("stringValue") ->
                value.get("stringValue").asString

            value.has("referenceValue") ->
                value.get("referenceValue").asString

            value.has("arrayValue") -> {
                val arrayValue =
                    value.getAsJsonObject("arrayValue")
                val values = arrayValue.get("values")

                if (values == null || !values.isJsonArray) {
                    emptyList<Any?>()
                } else {
                    values.asJsonArray.map(::decodeFirestoreValue)
                }
            }

            value.has("mapValue") -> {
                val mapValue =
                    value.getAsJsonObject("mapValue")
                val fields = mapValue.get("fields")

                if (fields == null || !fields.isJsonObject) {
                    emptyMap<String, Any?>()
                } else {
                    decodeFirestoreFields(fields.asJsonObject)
                }
            }

            else -> null
        }
    }

    private fun Map<String, Any?>.text(
        key: String
    ): String? {
        return this[key] as? String
    }

    private fun Map<String, Any?>.firstText(
        vararg keys: String
    ): String? {
        return keys.firstNotNullOfOrNull { key ->
            text(key)
        }
    }

    private fun Map<String, Any?>.number(
        key: String
    ): Number? {
        return this[key] as? Number
    }

    private fun Map<String, Any?>.mapList(
        key: String
    ): List<Map<String, Any?>> {
        val values = this[key] as? List<*>
            ?: return emptyList()

        return values.mapNotNull { value ->
            value.asStringMap()
        }
    }

    private fun Any?.asStringMap(): Map<String, Any?>? {
        val source = this as? Map<*, *>
            ?: return null

        return source.entries
            .mapNotNull { (key, value) ->
                val stringKey = key as? String
                    ?: return@mapNotNull null

                stringKey to value
            }
            .toMap()
    }

    private fun <T> Response<T>.requireBody(): T {
        if (!isSuccessful) {
            throw HttpException(this)
        }

        return body()
            ?: throw IllegalStateException(
                "Die API hat keine Antwortdaten geliefert."
            )
    }

    private data class Authentication(
        val idToken: String,
        val environment: String,
        val expiresAtEpochSeconds: Long
    )

    private companion object {
        const val LERBERMATT_STORE_ID = 41735
        const val LERBERMATT_MENU_ID = 18339
        const val LANGUAGE = "de-CH"

        const val TOKEN_EXPIRY_MARGIN_SECONDS = 60L
        const val DEFAULT_TOKEN_LIFETIME_SECONDS = 3600L

        const val FIREBASE_SIGN_IN_URL =
            "https://identitytoolkit.googleapis.com/v1/" +
                    "accounts:signInWithCustomToken?" +
                    "key=AIzaSyDR_h6fX9hBl2I7oy7xjzmYgDxJY2igdls"

        const val FIRESTORE_DOCUMENTS_URL =
            "https://firestore.googleapis.com/v1/projects/" +
                    "qnips-sv-group-ch/databases/(default)/documents"
    }
}