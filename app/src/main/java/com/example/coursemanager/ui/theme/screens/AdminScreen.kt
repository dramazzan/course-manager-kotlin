package com.example.coursemanager.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.coursemanager.data.Course
import com.example.coursemanager.viewmodel.MainViewModel

@Composable
fun AdminScreen(viewModel: MainViewModel, onLogout: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var selectedTeacherId by remember { mutableStateOf<Int?>(null) }
    var showManageUsers by remember { mutableStateOf(false) }

    // Фильтруем всех пользователей, выбирая только учителей
    val teachers = viewModel.users.value.filter { it.role == "TEACHER" }

    Column(modifier = Modifier.padding(16.dp)) {
        // Кнопка "Выйти"
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(onClick = onLogout) { Text("Выйти") }
        }

        if (showManageUsers) {
            ManageUsersScreen(viewModel = viewModel, onBack = { showManageUsers = false })
        } else {
            Text("Панель администратора", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // Форма создания курса с выбором учителя
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Название курса") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Выберите учителя:", style = MaterialTheme.typography.bodyLarge)
            teachers.forEach { teacher ->
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    RadioButton(
                        selected = (selectedTeacherId == teacher.id),
                        onClick = { selectedTeacherId = teacher.id }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = teacher.name)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                if (title.isNotBlank() && selectedTeacherId != null) {
                    viewModel.addCourse(title, selectedTeacherId!!)
                    title = ""
                    selectedTeacherId = null
                }
            }) {
                Text("Добавить курс")
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(viewModel.courses.value) { course: Course ->
                    CourseItem(course = course, onDelete = { viewModel.removeCourse(course) })
                    Divider()
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                viewModel.loadUsers()
                showManageUsers = true
            }) {
                Text("Управление пользователями")
            }
        }
    }
}

@Composable
fun CourseItem(course: Course, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "${course.title} (ID: ${course.id}) - Учитель: ${course.teacherId}")
        Button(onClick = onDelete) {
            Text("Удалить")
        }
    }
}
