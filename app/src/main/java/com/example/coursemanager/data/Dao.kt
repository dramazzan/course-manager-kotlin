package com.example.coursemanager.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Query("SELECT * FROM User WHERE email = :email AND password = :password LIMIT 1")
    fun login(email: String, password: String): User?

    @Query("SELECT * FROM User WHERE email = :email LIMIT 1")
    fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM User")
    fun getAllUsers(): List<User>

    @Query("SELECT * FROM User WHERE id = :userId LIMIT 1")
    fun getUserById(userId: Int): User?

    @Insert
    fun insert(user: User)

    @Update
    fun updateUser(user: User)
}

@Dao
interface CourseDao {
    @Query("SELECT * FROM Course WHERE teacherId = :teacherId")
    fun getCoursesForTeacher(teacherId: Int): List<Course>

    @Query("SELECT * FROM Course")
    fun getAllCourses(): List<Course>

    @Query("SELECT * FROM Course WHERE id = :courseId LIMIT 1")
    fun getCourseById(courseId: Int): Course?

    @Insert
    fun insert(course: Course)

    @Delete
    fun delete(course: Course)
}

@Dao
interface EnrollmentDao {
    @Query("SELECT * FROM Enrollment WHERE studentId = :studentId")
    fun getEnrollmentsForStudent(studentId: Int): List<Enrollment>

    @Query("SELECT * FROM Enrollment WHERE courseId = :courseId")
    fun getEnrollmentsForCourse(courseId: Int): List<Enrollment>

    @Insert
    fun insert(enrollment: Enrollment)
}

@Dao
interface MaterialDao {
    @Query("SELECT * FROM Material WHERE courseId = :courseId")
    fun getMaterialsForCourse(courseId: Int): List<Material>

    @Insert
    fun insert(material: Material)
}

@Dao
interface GradeDao {
    @Query("SELECT * FROM Grade WHERE studentId = :studentId")
    fun getGradesForStudent(studentId: Int): List<Grade>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(grade: Grade)
}

@Dao
interface TestDao {
    @Query("SELECT * FROM Test WHERE courseId = :courseId")
    fun getTestsForCourse(courseId: Int): List<Test>

    @Query("SELECT * FROM Test WHERE id = :testId LIMIT 1")
    fun getTestById(testId: Int): Test?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(test: Test): Long

    @Delete
    fun delete(test: Test)
}

@Dao
interface TestQuestionDao {
    @Query("SELECT * FROM TestQuestion WHERE testId = :testId ORDER BY orderNumber ASC")
    fun getQuestionsForTest(testId: Int): List<TestQuestion>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(question: TestQuestion)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(questions: List<TestQuestion>)

    @Delete
    fun delete(question: TestQuestion)

    @Query("DELETE FROM TestQuestion WHERE testId = :testId")
    fun deleteQuestionsForTest(testId: Int)
}

@Dao
interface TestResultDao {
    @Query("SELECT * FROM TestResult WHERE testId = :testId")
    fun getResultsForTest(testId: Int): List<TestResult>

    @Query("SELECT * FROM TestResult WHERE studentId = :studentId")
    fun getResultsForStudent(studentId: Int): List<TestResult>

    @Query("SELECT * FROM TestResult WHERE testId = :testId AND studentId = :studentId LIMIT 1")
    fun getStudentResultForTest(testId: Int, studentId: Int): TestResult?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(result: TestResult)

    @Delete
    fun delete(result: TestResult)
}
