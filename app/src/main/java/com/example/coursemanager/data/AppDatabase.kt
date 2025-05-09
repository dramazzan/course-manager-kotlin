package com.example.coursemanager.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [User::class, Course::class, Enrollment::class, Material::class, Grade::class, Test::class, TestQuestion::class, TestResult::class ],
    version = 7
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun courseDao(): CourseDao
    abstract fun enrollmentDao(): EnrollmentDao
    abstract fun materialDao(): MaterialDao
    abstract fun gradeDao(): GradeDao
    abstract fun testDao(): TestDao
    abstract fun testQuestionDao(): TestQuestionDao
    abstract fun testResultDao(): TestResultDao
}
