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
    var role: String
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

@Entity
data class Test(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val courseId: Int,
    val title: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity
data class TestQuestion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val testId: Int,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: String,
    val orderNumber: Int
)

@Entity
data class TestResult(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val testId: Int,
    val studentId: Int,
    val score: Float,
    val completedAt: Long = System.currentTimeMillis()
)
