package com.example.coursemanager.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String,
    var email: String,
    var password: String,
    var role: String  // "ADMIN", "TEACHER", "STUDENT"
)

@Entity
data class Course(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val teacherId: Int
)

@Entity
data class Enrollment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: Int,
    val courseId: Int
)

@Entity
data class Material(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val courseId: Int,
    val content: String
)

@Entity(indices = [Index(value = ["studentId", "courseId"], unique = true)])
data class Grade(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: Int,
    val courseId: Int,
    val grade: Float
)
