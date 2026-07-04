package com.example.gradetracker.repo

import androidx.compose.runtime.mutableStateListOf
import com.example.gradetracker.data.Grade
import com.example.gradetracker.data.SchoolYear
import com.example.gradetracker.data.Subject

object GradeRepository {
    val schoolYears = mutableStateListOf<SchoolYear>()
    val subjects = mutableStateListOf<Subject>()
    val grades = mutableStateListOf<Grade>()

    fun addSchoolYear(schoolYear: SchoolYear){
        schoolYears.add(schoolYear)
    }
    fun addSubject(subject: Subject){
        subjects.add(subject)
    }
    fun addGrade(grade: Grade){
        grades.add(grade)
    }


    fun getSubject(subjectId: String?): Subject?{
        return subjects.find{it.id == subjectId}
    }
    fun getGrade(gradeId: String): Grade?{
        return grades.find{it.id == gradeId}
    }
    fun getSchoolYear(schoolYearId: String?): SchoolYear?{
        return schoolYears.find{it.id == schoolYearId}
    }


    fun getGradesForSubject(subjectId: String?): List<Grade>{
        return grades.filter { it.subjectId == subjectId}
    }

    fun getSubjectsForSchoolYear(schoolYearId: String?): List<Subject>{
        return subjects.filter { it.schoolYearId == schoolYearId}
    }

    fun getGradesForSchoolYear(schoolYearId: String): List<Grade>{
        val subjectIds = subjects.filter { it.schoolYearId == schoolYearId }.map{it.id}
        return grades.filter { it.subjectId in subjectIds }
    }


    fun deleteGrade(gradeId: String){
        grades.removeIf { it.id == gradeId }
    }
    fun deleteSubject(subjectId: String){
        subjects.removeIf { it.id == subjectId }
    }
    fun deleteSchoolYear(schoolYearId: String){
        schoolYears.removeIf { it.id == schoolYearId }
    }


    fun getAllGrades(): List<Grade>{
        return grades
    }
    fun getAllSubjects(): List<Subject>{
        return subjects
    }
    fun getAllSchoolYears(): List<SchoolYear>{
        return  schoolYears
    }
}