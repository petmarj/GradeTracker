package com.example.gradetracker.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gradetracker.model.SchoolYear
import com.example.gradetracker.model.Subject
import com.example.gradetracker.model.Grade
import com.example.gradetracker.model.Student
import com.example.gradetracker.data.local.dao.GradeDao
import com.example.gradetracker.data.local.dao.SchoolYearDao
import com.example.gradetracker.data.local.dao.SubjectDao

@Database(
    entities = [SchoolYear::class, Subject::class, Grade::class],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun schoolYearDao(): SchoolYearDao
    abstract fun subjectDao(): SubjectDao
    abstract fun gradeDao(): GradeDao

}