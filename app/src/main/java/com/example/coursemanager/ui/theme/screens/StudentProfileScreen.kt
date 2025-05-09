package com.example.coursemanager.ui.theme.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.coursemanager.data.User
import com.example.coursemanager.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentProfileScreen(
    viewModel: MainViewModel,
    onBackPressed: () -> Unit
) {
    val currentUser = viewModel.currentUser
    var editMode by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf(currentUser?.name ?: "") }
    var email by remember { mutableStateOf(currentUser?.email ?: "") }

    var isSaving by remember { mutableStateOf(false) }
    var saveSuccess by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val nameRequester = remember { FocusRequester() }

    LaunchedEffect(editMode) {
        if (editMode) {
            delay(100)
            nameRequester.requestFocus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.primary,
                    titleContentColor = Color.White
                ),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Профиль студента",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (!editMode) {
                        IconButton(
                            onClick = { editMode = true },
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Редактировать",
                                tint = Color.White
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
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
                )
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileAvatar(
                name = currentUser?.name ?: "Студент",
                role = currentUser?.role ?: "STUDENT"
            )

            Spacer(modifier = Modifier.height(32.dp))

            StudentStatsCard(viewModel)

            Spacer(modifier = Modifier.height(32.dp))

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
                    Text(
                        text = "Личная информация",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ProfileFieldContent(
                        editMode = editMode,
                        label = "Имя",
                        value = name,
                        onValueChange = { name = it },
                        icon = Icons.Outlined.Person,
                        focusRequester = nameRequester
                    )

                    Divider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = AppColors.fieldBorder
                    )

                    ProfileFieldContent(
                        editMode = editMode,
                        label = "Email",
                        value = email,
                        onValueChange = { email = it },
                        icon = Icons.Outlined.Email,
                        keyboardType = KeyboardType.Email
                    )

                    Divider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = AppColors.fieldBorder
                    )


                    Divider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = AppColors.fieldBorder
                    )

                    ProfileFieldContent(
                        editMode = false,
                        label = "ID студента",
                        value = currentUser?.id?.toString() ?: "-",
                        onValueChange = { },
                        icon = Icons.Outlined.Badge
                    )

                    if (editMode) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    name = currentUser?.name ?: ""
                                    email = currentUser?.email ?: ""
                                    editMode = false
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = AppColors.primary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Отмена"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Отмена")
                            }

                            Button(
                                onClick = {
                                    currentUser?.let { user ->
                                        isSaving = true
                                        scope.launch {
                                            delay(1000)
                                            viewModel.updateUserProfile(
                                                user.copy(
                                                    name = name,
                                                    email = email,
                                                )
                                            )
                                            isSaving = false
                                            saveSuccess = true
                                            delay(1500)
                                            saveSuccess = false
                                            editMode = false
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AppColors.primary
                                ),
                                enabled = !isSaving
                            ) {
                                if (isSaving) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Save,
                                        contentDescription = "Сохранить"
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isSaving) "Сохранение..." else "Сохранить")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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
                    Text(
                        text = "Безопасность аккаунта",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    FilledTonalButton(
                        onClick = { /* Open password change dialog */ },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = AppColors.primary.copy(alpha = 0.1f),
                            contentColor = AppColors.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = "Сменить пароль"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Сменить пароль",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = saveSuccess,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "Профиль успешно обновлен",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileAvatar(name: String, role: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(contentAlignment = Alignment.Center) {
            val infiniteTransition = rememberInfiniteTransition(label = "border")
            val rotationAnim by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(10000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "rotation"
            )

            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.sweepGradient(
                            listOf(
                                AppColors.primary.copy(alpha = 0.8f),
                                AppColors.secondary.copy(alpha = 0.3f),
                                AppColors.primary.copy(alpha = 0.8f)
                            ),
                            center = androidx.compose.ui.geometry.Offset(0.5f, 0.5f)
                        )
                    )
                    .rotate(rotationAnim)
            )

            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
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
                    text = name.take(1).uppercase(),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = AppColors.textPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(AppColors.primary.copy(alpha = 0.1f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.School,
                    contentDescription = null,
                    tint = AppColors.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Студент",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.primary
                )
            }
        }
    }
}

@Composable
fun StudentStatsCard(viewModel: MainViewModel) {
    val enrolledCourses = viewModel.courses.value.size
    val completedCourses = viewModel.grades.value.count { it.grade >= 60 }
    val averageGrade = if (viewModel.grades.value.isNotEmpty()) {
        viewModel.grades.value.map { it.grade }.average().toFloat()
    } else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = AppColors.secondary.copy(alpha = 0.1f)
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
            Text(
                text = "Учебная статистика",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AppColors.textPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    value = enrolledCourses.toString(),
                    label = "Активные курсы",
                    icon = Icons.Outlined.MenuBook,
                    color = AppColors.secondary
                )

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(80.dp)
                        .background(AppColors.fieldBorder)
                )

                StatItem(
                    value = completedCourses.toString(),
                    label = "Завершено",
                    icon = Icons.Outlined.CheckCircle,
                    color = Color(0xFF4CAF50)
                )

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(80.dp)
                        .background(AppColors.fieldBorder)
                )
                StatItem(
                    value = String.format("%.1f", averageGrade),
                    label = "Ср. балл",
                    icon = Icons.Outlined.Star,
                    color = Color(0xFFFFC107)
                )
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String, icon: ImageVector, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = AppColors.textPrimary
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = AppColors.textSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ProfileFieldContent(
    editMode: Boolean,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    focusRequester: FocusRequester = FocusRequester()
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AppColors.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = AppColors.textSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            AnimatedContent(
                targetState = editMode,
                transitionSpec = {
                    fadeIn() + slideInVertically { it } togetherWith
                            fadeOut() + slideOutVertically { -it }
                }
            ) { isEditing ->
                if (isEditing) {
                    OutlinedTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.primary,
                            unfocusedBorderColor = AppColors.fieldBorder,
                            focusedTextColor = AppColors.textPrimary,
                            unfocusedTextColor = AppColors.textPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Text(
                        text = value.ifEmpty { "Не указано" },
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (value.isEmpty()) AppColors.textSecondary.copy(alpha = 0.5f) else AppColors.textPrimary
                    )
                }
            }
        }
    }
}