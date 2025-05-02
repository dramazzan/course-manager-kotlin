package com.example.coursemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import androidx.room.Room
import com.example.coursemanager.data.AppDatabase
import com.example.coursemanager.data.User
import com.example.coursemanager.ui.theme.screens.*
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
            val navController = rememberNavController()

            var currentUser by remember { mutableStateOf<User?>(null) }
            var showRegistration by remember { mutableStateOf(false) }

            // API key for Gemini (replace with your actual key)
            val apiKey = "YOUR_GEMINI_API_KEY"

            NavHost(navController = navController, startDestination = "login") {
                composable("login") {
                    LoginScreen(
                        onLoginSuccess = { user ->
                            currentUser = user
                            viewModel.setUser(user)
                            navController.navigate("home")
                        },
                        onRegisterClick = { showRegistration = true }
                    )
                }
                composable("registration") {
                    RegistrationScreen(
                        onRegistrationSuccess = { user ->
                            currentUser = user
                            viewModel.setUser(user)
                            navController.navigate("home")
                        },
                        onBack = { showRegistration = false }
                    )
                }
                composable("home") {
                    when (currentUser?.role) {
                        "ADMIN" -> AdminScreen(viewModel, onLogout = { currentUser = null })
                        "TEACHER" -> TeacherScreen(viewModel, onLogout = { currentUser = null })
                        "STUDENT" -> StudentScreen(viewModel, onLogout = { currentUser = null }, onChatClicked = {
                            navController.navigate("chat") // Переход в чат
                        })
                        else -> {}
                    }
                }
                composable("chat") {
                    // Передаем API ключ в экран чата
                    AssistantScreen(apiKey = "AIzaSyDU1KR3dRQVnWYMv5_7ylFMmt8gVywEYZ4") // Экран чата
                }
            }
        }
    }
}
