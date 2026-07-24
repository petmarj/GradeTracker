package com.example.gradetracker.logic

import com.example.gradetracker.data.Grade
import com.example.gradetracker.data.Subject
import com.example.gradetracker.repo.GradeRepository
import java.util.Locale.filter
import kotlin.math.round

object Calculator {

    fun getAverageForGrades(grades: List<Grade>): Double {


        val weightSum = grades.sumOf { it.weight }
        val weightedSum = grades.sumOf { it.weight * it.value }

        if (grades.isEmpty() || weightSum == 0.0) return 0.0
        return weightedSum / weightSum
    }

    fun getAverageForSchoolYear(subjects:List<Subject>, grades: List<Grade>): Double{
        var subjectAverageGradeSum: Double = 0.0
        var subjectCount: Int = 0
        for (subject in subjects) {
            val subjectGrades = grades.filter { grade -> grade.subjectId == subject.id }
            if (subjectGrades.isEmpty()){continue}
            subjectAverageGradeSum += getAverageForGrades(subjectGrades)
            subjectCount += 1
        }
        return if (subjectCount == 0){
            0.0
        } else {
            subjectAverageGradeSum / subjectCount
        }
    }


    fun neededGradeForGoal(grades: List<Grade>, goal: Double, weight: Double = 1.0): Double{


        val weightSum = grades.sumOf { it.weight }
        val weightedSum = grades.sumOf { it.weight * it.value }

        return goal * (weightSum + weight) - weightedSum
    }

    fun getPointsForSchoolYear(grades: List<Grade>, subjects: List<Subject>): Double{
        var points = 0.0
        for (subject in subjects){
            val subjectGrades = grades.filter { grade -> grade.subjectId == subject.id }
            if (subjectGrades.isEmpty()){continue}
            val durchschnitt = getAverageForGrades(subjectGrades)
            points +=   if(durchschnitt >= 4){
                            durchschnitt-4}
                        else {
                            2*(durchschnitt-4)
                        }

        }
        return points

    }


    fun roundToQuarter(x: Double): Double{
        return round(x*4)/4
    }
    fun roundToHalf(x: Double): Double{
        return round(x*2)/2
    }
    fun roundToTenth(x: Double): Double{
        return round(x*10)/10
    }
    fun roundToHundred(x: Double): Double{
        return round(x*100)/100
    }

}