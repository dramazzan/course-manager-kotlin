package com.example.coursemanager.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coursemanager.viewmodel.MainViewModel

@Composable
fun StudentScreen(viewModel: MainViewModel, onLogout: () -> Unit, onChatClicked: () -> Unit) {
    LaunchedEffect(Unit) {
        viewModel.loadAllCourses()
        viewModel.loadGrades()
    }

    val availableCourses = viewModel.allCourses.value.filter { availableCourse ->
        viewModel.courses.value.none { enrolled -> enrolled.id == availableCourse.id }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(onClick = onLogout) {
                    Text("Выйти")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("📚 Доступные курсы", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (availableCourses.isEmpty()) {
                Text(
                    text = "Нет доступных курсов",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(availableCourses) { course ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = course.title, fontSize = 18.sp)
                                Button(onClick = { viewModel.enrollCourse(course.id) }) {
                                    Text("Записаться")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("🎓 Мои курсы", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (viewModel.courses.value.isEmpty()) {
                Text(
                    text = "Вы еще не записаны ни на один курс",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(viewModel.courses.value) { course ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = course.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                val courseGrade = viewModel.grades.value.find { it.courseId == course.id }
                                Text(
                                    text = courseGrade?.let { "Оценка: ${it.grade}" } ?: "Оценка отсутствует",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка для перехода в чат
            Button(
                onClick = onChatClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Перейти в чат")
            }
        }
    }
}
