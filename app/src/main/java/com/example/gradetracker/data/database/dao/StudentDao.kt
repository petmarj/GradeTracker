package com.example.gradetracker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.gradetracker.data.Student

@Dao
interface StudentDao {
    @Query("SELECT * FROM Student LIMIT 1")
    suspend fun getStudent(): Student?

    @Upsert
    suspend fun upsert(student: Student)

    @Query("DELETE FROM Student")
    suspend fun clear()
}