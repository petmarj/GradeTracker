package com.example.gradetracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gradetracker.data.SchoolYear
import com.example.gradetracker.data.Subject
import com.example.gradetracker.data.database.dao.SchoolYearDao
import com.example.gradetracker.data.database.dao.SubjectDao

@Database(
    entities = [SchoolYear::class, Subject::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun schoolYearDao(): SchoolYearDao
    abstract fun subjectDao(): SubjectDao
}