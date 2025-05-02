package com.example.coursemanager.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coursemanager.data.User
import com.example.coursemanager.viewmodel.MainViewModel

@Composable
fun TeacherScreen(viewModel: MainViewModel, onLogout: () -> Unit) {
    var selectedCourseId by remember { mutableStateOf<Int?>(null) }
    var materialContent by remember { mutableStateOf("") }
    val gradeInputs = remember { mutableStateMapOf<Int, String>() }
    var enrolledStudents by remember { mutableStateOf<List<User>>(emptyList()) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onLogout) {
                    Text("Выйти")
                }
            }
            if (selectedCourseId == null) {
                Text("Курсы учителя", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(viewModel.courses.value) { course ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCourseId = course.id
                                    viewModel.loadMaterials(course.id)
                                    enrolledStudents = viewModel.loadStudentsForCourse(course.id)
                                    enrolledStudents.forEach { student ->
                                        if (gradeInputs[student.id] == null) {
                                            val currentGrade = viewModel.grades.value.find {
                                                it.studentId == student.id && it.courseId == course.id
                                            }
                                            gradeInputs[student.id] = currentGrade?.grade?.toString() ?: ""
                                        }
                                    }
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = course.title, fontSize = 18.sp)
                            }
                        }
                    }
                }
            } else {
                Text("Детали курса", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))

                Text("Материалы:", style = MaterialTheme.typography.headlineSmall)
                LazyColumn {
                    items(viewModel.materials.value) { mat ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Text(text = mat.content, modifier = Modifier.padding(16.dp))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = materialContent,
                    onValueChange = { materialContent = it },
                    label = { Text("Новый материал") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        selectedCourseId?.let { cid ->
                            viewModel.addMaterial(cid, materialContent)
                            materialContent = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Добавить материал")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Оценки студентов:", style = MaterialTheme.typography.headlineSmall)
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(enrolledStudents) { student ->
                        if (gradeInputs[student.id] == null) {
                            val currentGrade = viewModel.grades.value.find {
                                it.studentId == student.id && it.courseId == selectedCourseId
                            }
                            gradeInputs[student.id] = currentGrade?.grade?.toString() ?: ""
                        }
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
                                Column {
                                    Text(text = student.name, fontSize = 16.sp)
                                    Text(text = student.email, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                                }
                                OutlinedTextField(
                                    value = gradeInputs[student.id] ?: "",
                                    onValueChange = { gradeInputs[student.id] = it },
                                    label = { Text("Оценка") },
                                    modifier = Modifier.width(100.dp),
                                    singleLine = true
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
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        selectedCourseId = null
                        gradeInputs.clear()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Вернуться к курсам")
                }
            }
        }
    }
}
