package com.example.gradetracker.logic

import com.example.gradetracker.model.Grade
import com.example.gradetracker.repo.GradeRepository
import kotlin.math.round

object Calculator {
    fun getAverageForSubject(subjectId: String): Double {
        val grades = GradeRepository.getGradesForSubject(subjectId)

        val weightSum = grades.sumOf { it.weight }
        val weightedSum = grades.sumOf { it.weight * it.value }

        if (grades.isEmpty() || weightSum == 0.0) return 0.0
        return weightedSum / weightSum
    }

    fun getAverageForSchoolYear(schoolYearId: String): Double{
        val subjects = GradeRepository.getSubjectsForSchoolYear(schoolYearId)

        if (subjects.isEmpty()) return 0.0
        val subjectCount = subjects.size
        var subjectGradesSum = 0.0

        for (subject in subjects){
            subjectGradesSum += getAverageForSubject(subject.id)
        }
        return subjectGradesSum / subjectCount
    }

    fun neededGradeForGoal(subjectId: String, goal: Double, weight: Double = 1.0): Double{
        val grades = GradeRepository.getGradesForSubject(subjectId)

        val weightSum = grades.sumOf { it.weight }
        val weightedSum = grades.sumOf { it.weight * it.value }

        return goal * (weightSum + weight) - weightedSum
    }

    fun getPointsForSubject(subjectId: String): Double{
        val average = roundToHalf(getAverageForSubject(subjectId))
        return if (average >= 4) 6 - average
        else (4 - average) * 2
    }

    fun roundToQuarter(x: Double): Double{
        return round(x*4)/4
    }
    fun roundToHalf(x: Double): Double{
        return round(x*2)/2
    }

    fun getNumberOfGradesForSubject(subjectId: String): Int{
        return GradeRepository.getGradesForSubject(subjectId).size
    }
    fun getNumberOfGradesForSchoolYear(schoolYearId: String): Int{
        var count = 0
        for (subject in GradeRepository.getSubjectsForSchoolYear(schoolYearId)){
            count += getNumberOfGradesForSubject(subject.id)
        }
        return count
    }
    fun getNumberOfSubjectsForSchoolYear(schoolYearId: String): Int{
        return GradeRepository.getSubjectsForSchoolYear(schoolYearId).size
    }
}