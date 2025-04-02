package com.example.coursemanager.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.coursemanager.data.User
import com.example.coursemanager.viewmodel.MainViewModel

@Composable
fun TeacherScreen(viewModel: MainViewModel, onLogout: () -> Unit) {
    var selectedCourseId by remember { mutableStateOf<Int?>(null) }
    var materialContent by remember { mutableStateOf("") }
    // Состояние для ввода оценок: map: studentId -> введённое значение
    val gradeInputs = remember { mutableStateMapOf<Int, String>() }
    var enrolledStudents by remember { mutableStateOf<List<User>>(emptyList()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End) {
            Button(onClick = onLogout) { Text("Выйти") }
        }
        if (selectedCourseId == null) {
            Text("Курсы учителя", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(viewModel.courses.value) { course ->
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            selectedCourseId = course.id
                            viewModel.loadMaterials(course.id)
                            enrolledStudents = viewModel.loadStudentsForCourse(course.id)
                            // Инициализируем gradeInputs для каждого студента
                            enrolledStudents.forEach { student ->
                                if (gradeInputs[student.id] == null) {
                                    val currentGrade = viewModel.grades.value.find {
                                        it.studentId == student.id && it.courseId == course.id
                                    }
                                    gradeInputs[student.id] = currentGrade?.grade?.toString() ?: ""
                                }
                            }
                        }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = course.title)
                        }
                    }
                }
            }
        } else {
            Text("Детали курса", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            // Секция материалов
            Text("Материалы:")
            LazyColumn {
                items(viewModel.materials.value) { mat ->
                    Text(text = mat.content)
                    Divider()
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = materialContent,
                onValueChange = { materialContent = it },
                label = { Text("Новый материал") }
            )
            Button(onClick = {
                selectedCourseId?.let { cid ->
                    viewModel.addMaterial(cid, materialContent)
                    materialContent = ""
                }
            }) { Text("Добавить материал") }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Оценки для студентов:", style = MaterialTheme.typography.headlineSmall)
            LazyColumn {
                items(enrolledStudents) { student ->
                    // Если для студента еще нет значения в gradeInputs, заполнить его
                    if (gradeInputs[student.id] == null) {
                        val currentGrade = viewModel.grades.value.find {
                            it.studentId == student.id && it.courseId == selectedCourseId
                        }
                        gradeInputs[student.id] = currentGrade?.grade?.toString() ?: ""
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = student.name, modifier = Modifier.weight(1f))
                        TextField(
                            value = gradeInputs[student.id] ?: "",
                            onValueChange = { gradeInputs[student.id] = it },
                            label = { Text("Оценка") },
                            modifier = Modifier.width(100.dp)
                        )
                        Button(onClick = {
                            val gradeStr = gradeInputs[student.id]
                            val gradeVal = gradeStr?.toFloatOrNull() ?: 0f
                            selectedCourseId?.let { cid ->
                                viewModel.assignGrade(cid, student.id, gradeVal)
                                gradeInputs[student.id] = gradeVal.toString()
                            }
                        }) {
                            Text("Сохранить")
                        }
                    }
                    Divider()
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                selectedCourseId = null
                gradeInputs.clear()
            }) { Text("Вернуться к списку курсов") }
        }
    }
}
