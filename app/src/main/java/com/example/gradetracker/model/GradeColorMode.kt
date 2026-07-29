package com.example.gradetracker.model

enum class GradeColorMode(
    val redUntil: Double,
    val yellowAt: Double,
    val schoolYearRedRatio: Double,
    val schoolYearYellowRatio: Double
) {
    MILD(
        redUntil = 1.0,
        yellowAt = 4.0,
        schoolYearRedRatio = -0.5,
        schoolYearYellowRatio = 0.0
    ),
    NORMAL(
        redUntil = 2.0,
        yellowAt = 4.5,
        schoolYearRedRatio = -0.25,
        schoolYearYellowRatio = 0.25
    ),
    STRICT(
        redUntil = 2.5,
        yellowAt = 5.0,
        schoolYearRedRatio = 0.0,
        schoolYearYellowRatio = 0.5
    )
}
