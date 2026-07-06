package com.example.gradetracker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.gradetracker.data.SchoolYear

@Dao
interface SchoolYearDao {

    @Query("SELECT * FROM SchoolYear")
    fun getAll(): Flow<List<SchoolYear>>

    @Insert
    suspend fun insert(schoolYear: SchoolYear)

    @Query("DELETE FROM SchoolYear WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM SchoolYear WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): SchoolYear?
}