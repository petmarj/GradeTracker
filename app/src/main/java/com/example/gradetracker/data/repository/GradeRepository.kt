package com.example.gradetracker.data.repository

import androidx.room.withTransaction
import com.example.gradetracker.data.importer.PlusPointsParser
import com.example.gradetracker.data.local.database.AppDatabase
import com.example.gradetracker.model.Grade
import com.example.gradetracker.model.SchoolYear
import com.example.gradetracker.model.Subject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.io.InputStream

data class PlusPointsImportResult(
    val semesterName: String,
    val subjectCount: Int,
    val gradeCount: Int
)

class GradeRepository(
    private val db: AppDatabase
) {
    private val schoolYearDao = db.schoolYearDao()
    private val subjectDao = db.subjectDao()

    private val gradeDao = db.gradeDao()

    suspend fun importPlusPoints(inputStream: InputStream): PlusPointsImportResult {
        val imported = PlusPointsParser.parse(inputStream)
        var gradeCount = 0

        db.withTransaction {
            val schoolYear = SchoolYear(name = imported.name)
            schoolYearDao.insert(schoolYear)

            imported.subjects.forEach { importedSubject ->
                val subject = Subject(
                    name = importedSubject.name,
                    schoolYearId = schoolYear.id
                )
                subjectDao.insert(subject)

                importedSubject.exams.forEach { importedExam ->
                    gradeDao.insert(
                        Grade(
                            timeCreated = importedExam.date
                                ?: System.currentTimeMillis(),
                            subjectId = subject.id,
                            name = importedExam.name,
                            value = importedExam.mark,
                            weight = importedExam.weight
                        )
                    )
                    gradeCount++
                }
            }
        }

        return PlusPointsImportResult(
            semesterName = imported.name,
            subjectCount = imported.subjects.size,
            gradeCount = gradeCount
        )
    }



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
