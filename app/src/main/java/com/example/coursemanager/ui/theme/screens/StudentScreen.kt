package com.example.coursemanager.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.coursemanager.viewmodel.MainViewModel

@Composable
fun StudentScreen(viewModel: MainViewModel, onLogout: () -> Unit) {
    LaunchedEffect(Unit) {
        viewModel.loadAllCourses()
        viewModel.loadGrades()
    }

    val availableCourses = viewModel.allCourses.value.filter { availableCourse ->
        viewModel.courses.value.none { enrolled -> enrolled.id == availableCourse.id }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End) {
            Button(onClick = onLogout) { Text("Выйти") }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Доступные курсы", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(availableCourses) { course ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = course.title)
                    Button(onClick = { viewModel.enrollCourse(course.id) }) {
                        Text("Записаться")
                    }
                }
                Divider()
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Мои курсы", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(viewModel.courses.value) { course ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = course.title, style = MaterialTheme.typography.titleMedium)
                        val courseGrade = viewModel.grades.value.find { it.courseId == course.id }
                        if (courseGrade != null) {
                            Text(text = "Оценка: ${courseGrade.grade}", style = MaterialTheme.typography.bodyLarge)
                        } else {
                            Text(text = "Оценка отсутствует", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}
