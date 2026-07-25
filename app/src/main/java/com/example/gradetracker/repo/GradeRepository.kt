package com.example.gradetracker.repo

import com.example.gradetracker.data.Grade
import com.example.gradetracker.data.SchoolYear
import com.example.gradetracker.data.Subject
import com.example.gradetracker.data.database.AppDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GradeRepository(
    private val db: AppDatabase
) {
    private val schoolYearDao = db.schoolYearDao()
    private val subjectDao = db.subjectDao()

    private val gradeDao = db.gradeDao()



    suspend fun addSchoolYear(schoolYear: SchoolYear) {
        schoolYearDao.insert(schoolYear)
    }
    suspend fun addSubject(subject: Subject){
        subjectDao.insert(subject)
    }
    suspend fun addGrade(grade: Grade){
        gradeDao.insert(grade)
    }


    suspend fun getSubject(subjectId: String): Subject?{
        return subjectDao.getById(subjectId)
    }
    suspend fun getGrade(gradeId: String): Grade?{
        return gradeDao.getById(gradeId)
    }
    suspend fun getSchoolYear(schoolYearId: String?): SchoolYear? {
        return schoolYearDao.getById(schoolYearId)
    }


    fun getGradesForSubject(subjectId: String?): Flow<List<Grade>>{
        return gradeDao.getGradesBySubject(subjectId)
    }

    fun getSubjectsForSchoolYear(schoolYearId: String?): Flow<List<Subject>>{
        return subjectDao.getSubjectsBySchoolYear(schoolYearId)
    }

    fun getGradesForSchoolYear(schoolYearId: String): Flow<List<Grade>>{
        return combine(
            subjectDao.getSubjectsBySchoolYear(schoolYearId),
            gradeDao.getAll()) { subjects, grades ->

            val subjectIds = subjects.map { it.id }

            grades.filter { it.subjectId in subjectIds }
        }
    }


    suspend fun deleteGrade(gradeId: String?){
        gradeDao.deleteById(gradeId)
    }
    suspend fun deleteSubject(subjectId: String) {
        subjectDao.deleteById(subjectId)
    }
    suspend fun deleteSchoolYear(schoolYearId: String){
        schoolYearDao.deleteById(schoolYearId)
    }

    suspend fun updateGrade(grade: Grade) {
        gradeDao.update(grade)
    }
    suspend fun updateSubject(subject: Subject) {
        subjectDao.update(subject)
    }
    suspend fun updateSchoolYear(schoolYear: SchoolYear) {
        schoolYearDao.update(schoolYear)
    }
    fun getAllSchoolYears(): Flow<List<SchoolYear>> {
        return schoolYearDao.getAll()
    }
    fun getAllGrades(): Flow<List<Grade>>{
        return gradeDao.getAll()
    }
    fun getAllSubjects(): Flow<List<Subject>>{
        return subjectDao.getAll()
    }
}