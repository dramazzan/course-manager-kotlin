package com.example.coursemanager.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.coursemanager.data.*

class MainViewModel(private val db: AppDatabase) : ViewModel() {
    var currentUser: User? = null
        private set

    var courses = mutableStateOf<List<Course>>(listOf())
    var allCourses = mutableStateOf<List<Course>>(listOf())
    var materials = mutableStateOf<List<Material>>(listOf())
    var grades = mutableStateOf<List<Grade>>(listOf())
    var users = mutableStateOf<List<User>>(listOf())
    var tests = mutableStateOf<List<Test>>(listOf())
    private var _currentTest = mutableStateOf<Test?>(null)
//    val currentTest = _currentTest as State<Test?>)

    var currentTestQuestions = mutableStateOf<List<TestQuestion>>(listOf())

    fun setUser(user: User) {
        currentUser = user
        when (user.role) {
            "ADMIN" -> {
                loadCourses()
                loadUsers()
            }
            "TEACHER" -> {
                loadCourses()
            }
            "STUDENT" -> {
                val enrollments = db.enrollmentDao().getEnrollmentsForStudent(user.id)
                courses.value = enrollments.mapNotNull { enrollment ->
                    db.courseDao().getCourseById(enrollment.courseId)
                }
                loadGrades()
                loadAllCourses()
            }
        }
    }

    fun loadCourses() {
        currentUser?.let { user ->
            when (user.role) {
                "ADMIN" -> courses.value = db.courseDao().getAllCourses()
                "TEACHER" -> courses.value = db.courseDao().getCoursesForTeacher(user.id)
            }
        }
    }

    fun loadAllCourses() {
        allCourses.value = db.courseDao().getAllCourses()
    }

    fun loadGrades() {
        currentUser?.let { user ->
            if (user.role == "STUDENT") {
                grades.value = db.gradeDao().getGradesForStudent(user.id)
            }
        }
    }

    fun loadMaterials(courseId: Int) {
        materials.value = db.materialDao().getMaterialsForCourse(courseId)
    }

    fun loadUsers() {
        users.value = db.userDao().getAllUsers()
    }

    fun addCourse(title: String, teacherId: Int) {
        val newCourse = Course(title = title, teacherId = teacherId)
        db.courseDao().insert(newCourse)
        loadCourses()
    }

    fun removeCourse(course: Course) {
        db.courseDao().delete(course)
        loadCourses()
    }

    fun addMaterial(courseId: Int, content: String) {
        val newMaterial = Material(courseId = courseId, content = content)
        db.materialDao().insert(newMaterial)
        loadMaterials(courseId)
    }

    fun assignGrade(courseId: Int, studentId: Int, gradeValue: Float) {
        val newGrade = Grade(studentId = studentId, courseId = courseId, grade = gradeValue)
        db.gradeDao().insert(newGrade)
        loadGrades()
    }

    fun enrollCourse(courseId: Int) {
        currentUser?.let { user ->
            if (user.role == "STUDENT") {
                val enrollment = Enrollment(studentId = user.id, courseId = courseId)
                db.enrollmentDao().insert(enrollment)
                val enrollments = db.enrollmentDao().getEnrollmentsForStudent(user.id)
                courses.value = enrollments.mapNotNull { enrollment ->
                    db.courseDao().getCourseById(enrollment.courseId)
                }
            }
        }
    }

    fun updateUserRole(user: User, newRole: String) {
        user.role = newRole
        db.userDao().updateUser(user)
        loadUsers()
    }

    fun updateUserProfile(updatedUser: User) {
        db.userDao().updateUser(updatedUser)
        currentUser = updatedUser
    }

    fun loadStudentsForCourse(courseId: Int): List<User> {
        val enrollments = db.enrollmentDao().getEnrollmentsForCourse(courseId)
        return enrollments.mapNotNull { enrollment -> db.userDao().getUserById(enrollment.studentId) }
    }

    // Методы для работы с тестами
    fun createTest(courseId: Int, title: String, description: String, questions: List<TestQuestion>) {
        val newTest = Test(courseId = courseId, title = title, description = description)
        val testId = db.testDao().insert(newTest)
        val updatedQuestions = questions.map { it.copy(testId = testId.toInt()) }
        db.testQuestionDao().insertAll(updatedQuestions)
    }

    fun loadTestQuestions(testId: Int) {
        currentTestQuestions.value = db.testQuestionDao().getQuestionsForTest(testId)
    }

    fun setCurrentTest(test: Test) {
        _currentTest.value = test
        loadTestQuestions(test.id)
    }


    fun loadTests(courseId: Int) {
        tests.value = db.testDao().getTestsForCourse(courseId)
    }

    fun hasStudentTakenTest(testId: Int, studentId: Int): Boolean {
        return db.testResultDao().getStudentResultForTest(testId, studentId) != null
    }

    fun getStudentResultForTest(testId: Int, studentId: Int): TestResult? {
        return db.testResultDao().getStudentResultForTest(testId, studentId)
    }

    fun submitTestResult(testId: Int, studentId: Int, score: Float) {
        val result = TestResult(testId = testId, studentId = studentId, score = score)
        db.testResultDao().insert(result)
    }
}

class MainViewModelFactory(private val db: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
