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
fun LoginScreen(
    onLoginSuccess: (User) -> Unit,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Вход", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = password, onValueChange = { password = it }, label = { Text("Пароль") })
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            val database = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "course_db"
            ).allowMainThreadQueries().build()
            val user = database.userDao().login(email, password)
            if (user == null) {
                errorMessage = "Неверный email или пароль"
            } else {
                onLoginSuccess(user)
            }
        }) {
            Text("Войти")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRegisterClick) {
            Text("Регистрация")
        }
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}
