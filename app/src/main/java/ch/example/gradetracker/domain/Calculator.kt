package ch.example.gradetracker.domain

import com.example.gradetracker.model.Grade
import com.example.gradetracker.model.Subject
import kotlin.math.round

object Calculator {

    fun getAverageForGrades(grades: List<Grade>): Double? {


        val weightSum = grades.sumOf { it.weight }
        val weightedSum = grades.sumOf { it.weight * it.value }

        if (grades.isEmpty() || weightSum == 0.0) return null
        return weightedSum / weightSum
    }

    fun getAverageForSchoolYear(subjects: List<Subject>, grades: List<Grade>): Double? {
        var subjectAverageGradeSum: Double = 0.0
        var subjectCount: Int = 0
        for (subject in subjects) {
            val subjectGrades = grades.filter { grade -> grade.subjectId == subject.id }
            if (subjectGrades.isEmpty() || getAverageForGrades(subjectGrades) == null) {
                continue
            }
            getAverageForGrades(subjectGrades)?.let { subjectAverageGradeSum += it }
            subjectCount += 1
        }
        return if (subjectCount == 0) {
            null
        } else {
            subjectAverageGradeSum / subjectCount
        }
    }


    fun neededGradeForGoal(grades: List<Grade>, goal: Double, weight: Double = 1.0): Double? {
        if (weight <= 0.0) return null

        val weightSum = grades.sumOf { it.weight }
        val weightedSum = grades.sumOf { it.weight * it.value }

        return (
                goal * (weightSum + weight) - weightedSum
                ) / weight
    }

    fun getPointsForSchoolYear(grades: List<Grade>, subjects: List<Subject>): Double? {
        var points = 0.0
        var validSubjectCount = 0
        for (subject in subjects) {
            val subjectGrades = grades.filter { grade -> grade.subjectId == subject.id }
            if (subjectGrades.isEmpty()) {
                continue
            }
            val durchschnitt = roundToHalf(getAverageForGrades(subjectGrades)) ?: 0.0
            validSubjectCount += 1
            points += if (durchschnitt >= 4) {
                durchschnitt - 4
            } else {
                2 * (durchschnitt - 4)
            }

        }

        return if (validSubjectCount == 0) null else points

    }


    fun roundToQuarter(x: Double): Double {
        return round(x * 4) / 4
    }

    fun roundToHalf(x: Double?): Double? {
        return if (x != null) {
            round(x * 2) / 2
        } else {
            null
        }
    }

    fun roundToTenth(x: Double?): Double? {
        return if (x != null) {
            round(x * 10) / 10
        } else {
            null
        }
    }

    fun roundToHundred(x: Double?): Double? {
        return if (x != null) {
            round(x * 100) / 100
        } else {
            null
        }
    }

}