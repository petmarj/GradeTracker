package com.example.gradetracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gradetracker.data.SchoolYear
import com.example.gradetracker.data.Subject
import com.example.gradetracker.data.Grade
import com.example.gradetracker.data.Student
import com.example.gradetracker.data.database.dao.GradeDao
import com.example.gradetracker.data.database.dao.SchoolYearDao
import com.example.gradetracker.data.database.dao.StudentDao
import com.example.gradetracker.data.database.dao.SubjectDao

@Database(
    entities = [SchoolYear::class, Subject::class, Grade::class, Student::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun schoolYearDao(): SchoolYearDao
    abstract fun subjectDao(): SubjectDao
    abstract fun gradeDao(): GradeDao
    abstract fun studentDao(): StudentDao
}