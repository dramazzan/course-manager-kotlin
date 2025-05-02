package com.example.coursemanager.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val database = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "course_db"
    ).allowMainThreadQueries().build()

    /**
     * Функция для проверки корректности заполнения полей и регистрации пользователя.
     */
    fun registerUser() {
        errorMessage = when {
            name.trim().isEmpty() || email.trim().isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ->
                "Пожалуйста, заполните все поля"
            password != confirmPassword ->
                "Пароли не совпадают"
            database.userDao().getUserByEmail(email) != null ->
                "Пользователь с таким email уже существует"
            else -> {
                val newUser = User(
                    name = name,
                    email = email,
                    password = password,
                    role = "STUDENT"
                )
                database.userDao().insert(newUser)
                onRegistrationSuccess(newUser)
                return
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Регистрация",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Имя") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Повторите пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Button(
            onClick = { registerUser() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Зарегистрироваться")
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Назад")
        }
    }
}
