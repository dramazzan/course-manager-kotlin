package com.example.coursemanager.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [User::class, Course::class, Enrollment::class, Material::class, Grade::class],
    version = 6
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun courseDao(): CourseDao
    abstract fun enrollmentDao(): EnrollmentDao
    abstract fun materialDao(): MaterialDao
    abstract fun gradeDao(): GradeDao
}
