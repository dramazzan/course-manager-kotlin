package com.example.coursemanager.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coursemanager.data.Course
import com.example.coursemanager.viewmodel.MainViewModel

@Composable
fun AdminScreen(viewModel: MainViewModel, onLogout: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var selectedTeacherId by remember { mutableStateOf<Int?>(null) }
    var showManageUsers by remember { mutableStateOf(false) }

    val teachers = viewModel.users.value.filter { it.role == "TEACHER" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            OutlinedButton(onClick = onLogout) { Text("Выйти") }
        }

        if (showManageUsers) {
            ManageUsersScreen(viewModel = viewModel, onBack = { showManageUsers = false })
        } else {
            Text("Панель администратора", style = MaterialTheme.typography.headlineMedium, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Название курса") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Выберите учителя:", style = MaterialTheme.typography.bodyLarge)
                    teachers.forEach { teacher ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = (selectedTeacherId == teacher.id),
                                onClick = { selectedTeacherId = teacher.id }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = teacher.name)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    ElevatedButton(onClick = {
                        if (title.isNotBlank() && selectedTeacherId != null) {
                            viewModel.addCourse(title, selectedTeacherId!!)
                            title = ""
                            selectedTeacherId = null
                        }
                    }) {
                        Text("Добавить курс")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(viewModel.courses.value) { course: Course ->
                    CourseItem(course = course, onDelete = { viewModel.removeCourse(course) })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            ElevatedButton(onClick = {
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "${course.title} (ID: ${course.id}) - Учитель: ${course.teacherId}", fontSize = 16.sp)
            OutlinedButton(onClick = onDelete) {
                Text("Удалить")
            }
        }
    }
}
