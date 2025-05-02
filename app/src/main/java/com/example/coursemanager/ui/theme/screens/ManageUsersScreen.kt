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
import com.example.coursemanager.viewmodel.MainViewModel
import com.example.coursemanager.data.User

@Composable
fun ManageUsersScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val users = viewModel.users.value

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                "Управление пользователями",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(users) { user ->
                    UserItem(user = user, onChangeRole = { newRole ->
                        viewModel.updateUserRole(user, newRole)
                    })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Назад")
            }
        }
    }
}
@Composable
fun UserItem(user: User, onChangeRole: (String) -> Unit) {
    var role by remember { mutableStateOf(user.role) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = user.name, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    text = user.email,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "Роль: $role",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Button(
                onClick = {
                    val newRole = when (role) {
                        "STUDENT" -> "TEACHER"
                        "TEACHER" -> "STUDENT"
                        else -> role
                    }
                    role = newRole
                    onChangeRole(newRole)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Сменить роль")
            }
        }
    }
}
