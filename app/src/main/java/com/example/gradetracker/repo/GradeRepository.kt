package com.example.gradetracker.repo

import androidx.compose.runtime.mutableStateListOf
import com.example.gradetracker.data.Grade
import com.example.gradetracker.data.SchoolYear
import com.example.gradetracker.data.Subject
import com.example.gradetracker.data.database.AppDatabase
import kotlinx.coroutines.flow.Flow

class GradeRepository(
    private val db: AppDatabase
) {
    private val schoolYearDao = db.schoolYearDao()
    private val subjectDao = db.subjectDao()
    val grades = mutableStateListOf<Grade>()


    suspend fun addSchoolYear(schoolYear: SchoolYear) {
        schoolYearDao.insert(schoolYear)
    }
    suspend fun addSubject(subject: Subject){
        subjectDao.insert(subject)
    }
    fun addGrade(grade: Grade){
        grades.add(grade)
    }


    suspend fun getSubject(subjectId: String): Subject?{
        return subjectDao.getById(subjectId)
    }
    fun getGrade(gradeId: String): Grade?{
        return grades.find{it.id == gradeId}
    }
    suspend fun getSchoolYear(schoolYearId: String): SchoolYear? {
        return schoolYearDao.getById(schoolYearId)
    }


    fun getGradesForSubject(subjectId: String?): List<Grade>{
        return grades.filter { it.subjectId == subjectId}
    }

    suspend fun getSubjectsForSchoolYear(schoolYearId: String?): List<Subject>{
        return subjectDao.getSubjectsBySchoolYearId(schoolYearId)
    }

    suspend fun getGradesForSchoolYear(schoolYearId: String): List<Grade>{
        val subjectIds = subjectDao.getSubjectsBySchoolYearId(schoolYearId).map{it.id}
        return grades.filter { it.subjectId in subjectIds }
    }


    fun deleteGrade(gradeId: String){
        grades.removeIf { it.id == gradeId }
    }
    suspend fun deleteSubject(subjectId: String) {
        subjectDao.deleteById(subjectId)
    }
    suspend fun deleteSchoolYear(schoolYearId: String){
        schoolYearDao.deleteById(schoolYearId)
    }

    fun getAllSchoolYears(): Flow<List<SchoolYear>> {
        return schoolYearDao.getAll()
    }
    fun getAllGrades(): List<Grade>{
        return grades
    }
    fun getAllSubjects(): Flow<List<Subject>>{
        return subjectDao.getAll()
    }
}