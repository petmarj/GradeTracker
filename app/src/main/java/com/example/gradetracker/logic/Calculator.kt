package com.example.gradetracker.logic

import com.example.gradetracker.data.Grade
import com.example.gradetracker.repo.GradeRepository
import kotlin.math.round

object Calculator {

    fun getAverageForGrades(grades: List<Grade>): Double {


        val weightSum = grades.sumOf { it.weight }
        val weightedSum = grades.sumOf { it.weight * it.value }

        if (grades.isEmpty() || weightSum == 0.0) return 0.0
        return weightedSum / weightSum
    }



    fun neededGradeForGoal(grades: List<Grade>, goal: Double, weight: Double = 1.0): Double{


        val weightSum = grades.sumOf { it.weight }
        val weightedSum = grades.sumOf { it.weight * it.value }

        return goal * (weightSum + weight) - weightedSum
    }

    fun getPointsForGrades(grades: List<Grade>): Double{
        val average = roundToHalf(getAverageForGrades(grades))
        return if (average >= 4) average - 4
        else (average - 4) * 2
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