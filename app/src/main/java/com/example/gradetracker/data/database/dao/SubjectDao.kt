package com.example.gradetracker.data.database.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.example.gradetracker.data.Subject

@Dao
interface SubjectDao {

    @Query("SELECT * FROM Subject")
    fun getAll(): Flow<List<Subject>>

    @Insert
    suspend fun insert(subject: Subject)

    @Query("DELETE FROM Subject WHERE id = :id")
    suspend fun deleteById(id: String)

    @Update
    suspend fun update(subject: Subject)

    @Query("SELECT * FROM Subject WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): Subject?

    @Query("SELECT * FROM Subject WHERE schoolYearId = :id")
    fun getSubjectsBySchoolYear(id: String?): Flow<List<Subject>>
}