package com.example.gradetracker.model

import java.time.LocalDate
import java.time.temporal.WeekFields

enum class HolidayType(
    val displayName: String
) {
    FEBRUARY("Sportferien"),
    SPRING("Frühlingsferien"),
    SUMMER("Sommerferien"),
    AUTUMN("Herbstferien"),
    WINTER("Winterferien")
}


fun holidayForWeek(
    weekStart: LocalDate
): HolidayType? {
    val weekFields = WeekFields.ISO

    val weekNumber = weekStart.get(
        weekFields.weekOfWeekBasedYear()
    )

    val weekBasedYear = weekStart.get(
        weekFields.weekBasedYear()
    )

    val previousYearHad53Weeks =
        numberOfIsoWeeks(weekBasedYear - 1) == 53

    val summerWeeks = if (previousYearHad53Weeks) {
        27..32
    } else {
        28..32
    }

    val lastWeekOfYear = numberOfIsoWeeks(weekBasedYear)

    return when {
        weekNumber == 6 ->
            HolidayType.FEBRUARY

        weekNumber in 15..16 ->
            HolidayType.SPRING

        weekNumber in summerWeeks ->
            HolidayType.SUMMER

        weekNumber in 39..41 ->
            HolidayType.AUTUMN

        weekNumber == 1 ||
                weekNumber == lastWeekOfYear ->
            HolidayType.WINTER

        else -> null
    }
}

fun numberOfIsoWeeks(year: Int): Int {
    return LocalDate
        .of(year, 12, 28)
        .get(WeekFields.ISO.weekOfWeekBasedYear())
}