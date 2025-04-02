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
fun ManageUsersScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val users = viewModel.users.value

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Управление пользователями", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(users) { user ->
                UserItem(user = user, onChangeRole = { newRole ->
                    viewModel.updateUserRole(user, newRole)
                })
                Divider()
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text("Назад")
        }
    }
}

@Composable
fun UserItem(user: com.example.coursemanager.data.User, onChangeRole: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "${user.name} (${user.email}) - ${user.role}")
        Button(onClick = {
            val newRole = when (user.role) {
                "STUDENT" -> "TEACHER"
                "TEACHER" -> "STUDENT"
                else -> user.role  // не меняем для ADMIN
            }
            onChangeRole(newRole)
        }) {
            Text("Сменить роль")
        }
    }
}
