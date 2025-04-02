package com.example.coursemanager.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.coursemanager.data.AppDatabase
import com.example.coursemanager.data.User

@Composable
fun RegistrationScreen(
    onRegistrationSuccess: (User) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    val database = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "course_db"
    ).allowMainThreadQueries().build()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Регистрация", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = name, onValueChange = { name = it }, label = { Text("Имя") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = password, onValueChange = { password = it }, label = { Text("Пароль") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Повторите пароль") })
        Spacer(modifier = Modifier.height(8.dp))
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }
        Button(onClick = {
            if (name.trim().isEmpty() || email.trim().isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                errorMessage = "Пожалуйста, заполните все поля"
            } else if (password != confirmPassword) {
                errorMessage = "Пароли не совпадают"
            } else {
                if (database.userDao().getUserByEmail(email) != null) {
                    errorMessage = "Пользователь с таким email уже существует"
                } else {
                    val newUser = User(
                        name = name,
                        email = email,
                        password = password,
                        role = "STUDENT"
                    )
                    database.userDao().insert(newUser)
                    onRegistrationSuccess(newUser)
                }
            }
        }) {
            Text("Зарегистрироваться")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onBack) {
            Text("Назад")
        }
    }
}
