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
import com.example.coursemanager.ui.theme.CourseManagerTheme
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
            CourseManagerTheme {
                val viewModel: MainViewModel = viewModel(factory = MainViewModelFactory(database))
                val navController = rememberNavController()

                var currentUser by remember { mutableStateOf<User?>(null) }
                var showRegistration by remember { mutableStateOf(false) }

                val apiKey = "YOUR_GEMINI_API_KEY"

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = { user ->
                                currentUser = user
                                viewModel.setUser(user)
                                navController.navigate("home")
                            },
                            onRegisterClick = {
                                navController.navigate("registration")
                            }
                        )
                    }
                    composable("registration") {
                        RegistrationScreen(
                            onRegistrationSuccess = { user ->
                                currentUser = user
                                viewModel.setUser(user)
                                navController.navigate("home")
                            },
                            onBack = {
                                navController.navigate("login")
                            }
                        )
                    }
                    composable("home") {
                        when (currentUser?.role) {
                            "ADMIN" -> AdminScreen(viewModel, onLogout = {
                                currentUser = null
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            })

                            "TEACHER" -> TeacherScreen(viewModel, onLogout = {
                                currentUser = null
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            })

                            "STUDENT" -> StudentScreen(viewModel, onLogout = {
                                currentUser = null
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }, onChatClicked = {
                                navController.navigate("chat")
                            }
                                )

                            else -> {}
                        }
                    }

                    composable("chat") {
                        AssistantScreen(apiKey = BuildConfig.GEMINI_API_KEY , onBackPressed ={
                            navController.navigate("home"){
                                popUpTo("chat"){inclusive = true}
                            }

                        })
                    }
                }
            }
        }
    }
}
