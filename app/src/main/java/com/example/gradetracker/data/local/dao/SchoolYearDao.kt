package com.example.gradetracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.example.gradetracker.model.SchoolYear

@Dao
interface SchoolYearDao {

    @Query("SELECT * FROM SchoolYear")
    fun getAll(): Flow<List<SchoolYear>>

    @Insert
    suspend fun insert(schoolYear: SchoolYear)

    @Update
    suspend fun update(schoolYear: SchoolYear)
    @Query("DELETE FROM SchoolYear WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM SchoolYear WHERE id = :id LIMIT 1")
    suspend fun getById(id: String?): SchoolYear?
}