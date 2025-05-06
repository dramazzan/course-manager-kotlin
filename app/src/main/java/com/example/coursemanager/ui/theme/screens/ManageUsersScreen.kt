package com.example.coursemanager.ui.theme.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.coursemanager.viewmodel.MainViewModel
import com.example.coursemanager.data.User
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUsersScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val users = viewModel.users.value
    val userStates = remember { mutableStateListOf<Boolean>().apply {
        addAll(List(users.size) { false })
    } }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            "Управление пользователями",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            "${users.size} пользователей в системе",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        }
    ) { paddingValues ->
        if (users.isEmpty()) {
            EmptyUsersContent(modifier = Modifier.padding(paddingValues))
        } else {
            LaunchedEffect(Unit) {
                // Анимация появления карточек
                users.forEachIndexed { index, _ ->
                    delay(100L * index)
                    if (index < userStates.size) {
                        userStates[index] = true
                    }
                }
            }

            UsersContent(
                users = users,
                userStates = userStates,
                onChangeRole = { user, newRole ->
                    viewModel.updateUserRole(user, newRole)
                },
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
private fun EmptyUsersContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(120.dp),
                tonalElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Список пользователей пуст",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Здесь будут отображаться добавленные пользователи системы",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun UsersContent(
    users: List<User>,
    userStates: List<Boolean>,
    onChangeRole: (User, String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(users, key = { _, user -> user.email }) { index, user ->
            val visible = if (index < userStates.size) userStates[index] else true

            AnimatedVisibility(
                visibleState = remember { MutableTransitionState(false) }
                    .apply { targetState = visible },
                enter = fadeIn(animationSpec = tween(300)) +
                        slideInVertically(
                            animationSpec = tween(300),
                            initialOffsetY = { it / 5 }
                        )
            ) {
                UserCard(
                    user = user,
                    onChangeRole = { newRole -> onChangeRole(user, newRole) }
                )
            }
        }
    }
}

@Composable
fun UserCard(user: User, onChangeRole: (String) -> Unit) {
    var role by remember { mutableStateOf(user.role) }

    val (roleIcon, roleText, roleColor) = when (role) {
        "TEACHER" -> Triple(
            Icons.Outlined.School,
            "Преподаватель",
            MaterialTheme.colorScheme.tertiary
        )
        "STUDENT" -> Triple(
            Icons.Default.Person,
            "Студент",
            MaterialTheme.colorScheme.secondary
        )
        else -> Triple(
            Icons.Default.Person,
            role,
            MaterialTheme.colorScheme.primary
        )
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Аватар пользователя
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = roleColor.copy(alpha = 0.1f),
                modifier = Modifier.size(56.dp),
                border = BorderStroke(1.dp, roleColor.copy(alpha = 0.2f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = roleIcon,
                        contentDescription = null,
                        tint = roleColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Информация о пользователе
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = roleColor.copy(alpha = 0.12f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = roleIcon,
                                contentDescription = null,
                                tint = roleColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = roleText,
                                style = MaterialTheme.typography.labelMedium,
                                color = roleColor
                            )
                        }
                    }
                }
            }

            // Кнопка смены роли
            FilledTonalIconButton(
                onClick = {
                    val newRole = when (role) {
                        "STUDENT" -> "TEACHER"
                        "TEACHER" -> "STUDENT"
                        else -> role
                    }
                    role = newRole
                    onChangeRole(newRole)
                },
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.SwapHoriz,
                    contentDescription = "Сменить роль"
                )
            }
        }
    }
}