package com.example.coursemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.coursemanager.data.AppDatabase
import com.example.coursemanager.data.User
import com.example.coursemanager.ui.theme.screens.AdminScreen
import com.example.coursemanager.ui.theme.screens.LoginScreen
import com.example.coursemanager.ui.theme.screens.RegistrationScreen
import com.example.coursemanager.ui.theme.screens.StudentScreen
import com.example.coursemanager.ui.theme.screens.TeacherScreen
import com.example.coursemanager.viewmodel.MainViewModel
import com.example.coursemanager.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "course_db"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()

        if (database.userDao().getUserByEmail("admin@example.com") == null) {
            val admin = com.example.coursemanager.data.User(
                name = "Администратор",
                email = "admin@example.com",
                password = "adminpass",
                role = "ADMIN"
            )
            database.userDao().insert(admin)
        }

        setContent {
            val viewModel: MainViewModel = viewModel(factory = MainViewModelFactory(database))
            var currentUser by remember { mutableStateOf<User?>(null) }
            var showRegistration by remember { mutableStateOf(false) }

            if (currentUser == null) {
                if (showRegistration) {
                    RegistrationScreen(
                        onRegistrationSuccess = { user ->
                            currentUser = user
                            viewModel.setUser(user)
                        },
                        onBack = { showRegistration = false }
                    )
                } else {
                    LoginScreen(
                        onLoginSuccess = { user ->
                            currentUser = user
                            viewModel.setUser(user)
                        },
                        onRegisterClick = { showRegistration = true }
                    )
                }
            } else {
                when (currentUser?.role) {
                    "ADMIN" -> AdminScreen(viewModel, onLogout = { currentUser = null })
                    "TEACHER" -> TeacherScreen(viewModel, onLogout = { currentUser = null })
                    "STUDENT" -> StudentScreen(viewModel, onLogout = { currentUser = null })
                    else -> {}
                }
            }
        }
    }
}
