package com.example.coursemanager.ui.theme.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.coursemanager.data.Course
import com.example.coursemanager.viewmodel.MainViewModel


@Composable
fun AdminStatsCard(courseCount: Int, userCount: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = AppColors.primary.copy(alpha = 0.1f)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    AppColors.primary,
                                    AppColors.secondary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AdminPanelSettings,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Панель администратора",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AdminStatisticItem(
                    icon = Icons.Default.School,
                    title = "Курсы",
                    value = courseCount.toString(),
                    color = AppColors.primary
                )

                AdminStatisticItem(
                    icon = Icons.Default.Person,
                    title = "Пользователи",
                    value = userCount.toString(),
                    color = AppColors.secondary
                )
            }
        }
    }
}

@Composable
fun AdminStatisticItem(icon: ImageVector, title: String, value: String, color: Color) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = color.copy(alpha = 0.1f)
            ),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.textSecondary
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
@Composable
fun CourseItem(course: Course, onDelete: () -> Unit, index: Int) {
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 50L) // Сократил задержку для быстрого появления
        expanded = true
    }

    AnimatedVisibility(
        visible = expanded,
        enter = fadeIn(animationSpec = tween(200)) +
                slideInHorizontally(animationSpec = tween(200)) { it / 3 }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp), // Уменьшил вертикальный padding для компактности
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 2.dp, // Уменьшил тень для более плоского дизайна
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp), // Уменьшил внутренний padding
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Аватар курса
                Box(
                    modifier = Modifier
                        .size(40.dp) // Уменьшил размер аватара
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    AppColors.primary,
                                    AppColors.secondary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = course.title.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Информация о курсе
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = course.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            tint = AppColors.textSecondary,
                            modifier = Modifier.size(14.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "ID: ${course.teacherId}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.textSecondary
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(AppColors.textSecondary.copy(alpha = 0.3f))
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "ID курса: ${course.id}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.textSecondary
                        )
                    }
                }

                // Кнопка удаления
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AppColors.error.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = AppColors.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(viewModel: MainViewModel, onLogout: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var selectedTeacherId by remember { mutableStateOf<Int?>(null) }
    var showManageUsers by remember { mutableStateOf(false) }
    var expandAddCourseCard by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") } // Добавляем поиск

    val teachers = viewModel.users.value.filter { it.role == "TEACHER" }

    // Фильтрация курсов по поисковому запросу
    val filteredCourses = remember(viewModel.courses.value, searchQuery) {
        if (searchQuery.isBlank()) {
            viewModel.courses.value
        } else {
            viewModel.courses.value.filter {
                it.title.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.AdminPanelSettings,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (showManageUsers) "Управление пользователями" else "Панель администратора",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                navigationIcon = {
                    if (showManageUsers) {
                        IconButton(
                            onClick = { showManageUsers = false },
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AppColors.backgroundTop,
                            AppColors.backgroundBottom
                        )
                    )
                ),
            color = Color.Transparent
        ) {
            AnimatedVisibility(
                visible = !showManageUsers,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp) // Уменьшил вертикальный padding
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    AppColors.backgroundTop,
                                    AppColors.backgroundBottom
                                )
                            )
                        )
                ) {
                    // Admin Stats Card
                    AdminStatsCard(courseCount = viewModel.courses.value.size, userCount = viewModel.users.value.size)

                    Spacer(modifier = Modifier.height(16.dp)) // Уменьшил интервал

                    // Add Course Button (компактная версия)
                    ElevatedButton(
                        onClick = { expandAddCourseCard = !expandAddCourseCard },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = Color.White,
                            contentColor = AppColors.secondary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add course",
                            tint = AppColors.secondary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Добавить новый курс",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = if (expandAddCourseCard)
                                Icons.Filled.KeyboardArrowUp
                            else
                                Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Expand",
                            tint = AppColors.secondary
                        )
                    }

                    // Форма добавления курса
                    AnimatedVisibility(
                        visible = expandAddCourseCard,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                OutlinedTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = title,
                                    onValueChange = { title = it },
                                    label = { Text(text = "Название курса") },
                                    singleLine = true,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedIndicatorColor = AppColors.secondary,
                                        unfocusedIndicatorColor = AppColors.fieldBorder,
                                        focusedLabelColor = AppColors.secondary,
                                        unfocusedLabelColor = AppColors.textSecondary,
                                        cursorColor = AppColors.secondary
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Выберите учителя:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = AppColors.textPrimary
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp), // Уменьшил высоту
                                    colors = CardDefaults.cardColors(
                                        containerColor = AppColors.backgroundTop
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    if (teachers.isEmpty()) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Нет доступных преподавателей",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = AppColors.textSecondary,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    } else {
                                        LazyColumn(
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        ) {
                                            items(teachers) { teacher ->
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                                ) {
                                                    RadioButton(
                                                        selected = (selectedTeacherId == teacher.id),
                                                        onClick = { selectedTeacherId = teacher.id },
                                                        colors = RadioButtonDefaults.colors(
                                                            selectedColor = AppColors.secondary,
                                                            unselectedColor = AppColors.textSecondary
                                                        )
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Column {
                                                        Text(
                                                            text = teacher.name,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            fontWeight = FontWeight.Medium,
                                                            color = AppColors.textPrimary
                                                        )
                                                        Text(
                                                            text = teacher.email,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = AppColors.textSecondary
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                FilledTonalButton(
                                    onClick = {
                                        if (title.isNotBlank() && selectedTeacherId != null) {
                                            viewModel.addCourse(title, selectedTeacherId!!)
                                            title = ""
                                            selectedTeacherId = null
                                            expandAddCourseCard = false
                                        }
                                    },
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .height(44.dp),
                                    enabled = title.isNotBlank() && selectedTeacherId != null,
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = AppColors.secondary,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Создать курс",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Поисковая строка для курсов
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Поиск курсов...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = AppColors.textSecondary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = AppColors.textSecondary
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = AppColors.primary,
                            unfocusedIndicatorColor = AppColors.fieldBorder,
                            cursorColor = AppColors.primary
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Заголовок списка курсов с количеством
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = AppColors.primary,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Список курсов",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.textPrimary
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AppColors.primary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = " ${filteredCourses.size} из ${viewModel.courses.value.size} ",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Список курсов - основное изменение для улучшения скроллинга
                    if (viewModel.courses.value.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(24.dp)
                                    .clip(RoundedCornerShape(28.dp))
                                    .background(Color.White)
                                    .padding(24.dp)
                            ) {
                                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                                val scale by infiniteTransition.animateFloat(
                                    initialValue = 0.9f,
                                    targetValue = 1.1f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(1500),
                                        repeatMode = RepeatMode.Reverse
                                    ),
                                    label = "scale"
                                )

                                Icon(
                                    imageVector = Icons.Outlined.School,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .scale(scale),
                                    tint = AppColors.textSecondary.copy(alpha = 0.7f)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Нет доступных курсов",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = AppColors.textPrimary,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Создайте новый курс с помощью формы выше",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AppColors.textSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else if (filteredCourses.isEmpty()) {
                        // Если есть курсы, но ничего не найдено по запросу
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(24.dp)
                                    .clip(RoundedCornerShape(28.dp))
                                    .background(Color.White)
                                    .padding(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SearchOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = AppColors.textSecondary.copy(alpha = 0.7f)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Ничего не найдено",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = AppColors.textPrimary,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Попробуйте изменить поисковый запрос",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AppColors.textSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp), // Уменьшил интервал между элементами
                            modifier = Modifier.weight(1f)
                        ) {
                            itemsIndexed(filteredCourses) { index, course ->
                                CourseItem(
                                    course = course,
                                    onDelete = { viewModel.removeCourse(course) },
                                    index = index
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // User Management Button
                    FilledTonalButton(
                        onClick = {
                            viewModel.loadUsers()
                            showManageUsers = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = AppColors.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Users",
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Управление пользователями",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = showManageUsers,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                ManageUsersScreen(viewModel = viewModel, onBack = { showManageUsers = false })
            }
        }
    }
}