package com.example.coursemanager.ui.theme.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.navigation.NavHostController
import com.example.coursemanager.data.Course
import com.example.coursemanager.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentScreen(
    viewModel: MainViewModel,
    onLogout: () -> Unit,
    onChatClicked: () -> Unit,
    onProfile: () -> Unit,
    onCourseClick: (Int) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.loadAllCourses()
        viewModel.loadGrades()
    }

    val availableCourses = viewModel.allCourses.value.filter { availableCourse ->
        viewModel.courses.value.none { enrolled -> enrolled.id == availableCourse.id }
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Доступные курсы", "Мои курсы")

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
                            imageVector = Icons.Rounded.School,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Портал студента",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                actions = {
                    // Кнопка "Профиль"
                    IconButton(
                        onClick = onProfile,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Профиль",
                            tint = Color.White
                        )
                    }

                    // Кнопка "Выйти"
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Выйти",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onChatClicked,
                containerColor = AppColors.secondary,
                shape = RoundedCornerShape(28.dp),
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Перейти в чат",
                        tint = Color.White
                    )
                    AnimatedVisibility(
                        visible = true,
                        enter = expandHorizontally() + fadeIn(),
                        exit = shrinkHorizontally() + fadeOut()
                    ) {
                        Row {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Чат",
                                color = Color.White,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
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
        ) {
            // Custom Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = AppColors.primary,
                divider = {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(Color.White)
                    )
                },
                indicator = { tabPositions ->
                    Box(
                        Modifier
                            .tabIndicatorOffset(tabPositions[selectedTab])
                            .height(4.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        AppColors.primary,
                                        AppColors.secondary
                                    )
                                ),
                                shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                            )
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    val selected = selectedTab == index
                    Tab(
                        selected = selected,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) AppColors.primary else AppColors.textSecondary
                            )
                        },
                        icon = {
                            val icon = if (index == 0) Icons.Outlined.MenuBook else Icons.Default.School
                            val tint = if (selected) AppColors.primary else AppColors.textSecondary

                            Icon(
                                imageVector = icon,
                                contentDescription = title,
                                tint = tint
                            )
                        },
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }

            // Content area with animated transitions
            Box(modifier = Modifier.fillMaxSize()) {
                this@Column.AnimatedVisibility(
                    visible = selectedTab == 0,
                    enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )),
                    exit = fadeOut(animationSpec = tween(300)) + shrinkVertically()
                ) {
                    AvailableCoursesSection(
                        availableCourses, viewModel
                    )
                }

                this@Column.AnimatedVisibility(
                    visible = selectedTab == 1,
                    enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )),
                    exit = fadeOut(animationSpec = tween(300)) + shrinkVertically()
                ) {
                    EnrolledCoursesSection(
                        viewModel = viewModel,
                        onCourseSelected = { courseId -> onCourseClick(courseId) }
                    )
                }
            }
        }
    }
}
@Composable
fun AvailableCoursesSection(availableCourses: List<Course>, viewModel: MainViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CourseStatusCard(
            icon = Icons.Outlined.MenuBook,
            title = "Доступные курсы",
            count = availableCourses.size,
            color = AppColors.secondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (availableCourses.isEmpty()) {
            EmptyStateMessage("Нет доступных курсов")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp), // Add padding for FAB
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(availableCourses) { index, course ->
                    AvailableCourseItem(
                        course = course,
                        onEnroll = { viewModel.enrollCourse(course.id) },
                        animationDelay = index * 100
                    )
                }
            }
        }
    }
}

@Composable
fun EnrolledCoursesSection(viewModel: MainViewModel, onCourseSelected: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CourseStatusCard(
            icon = Icons.Default.School,
            title = "Мои курсы",
            count = viewModel.courses.value.size,
            color = AppColors.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.courses.value.isEmpty()) {
            EmptyStateMessage("Вы еще не записаны ни на один курс")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp), // Add padding for FAB
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(viewModel.courses.value) { index, course ->
                    val courseGrade = viewModel.grades.value.find { it.courseId == course.id }
                    EnrolledCourseItem(
                        course = course,
                        grade = courseGrade?.grade,
                        animationDelay = index * 100,
                        onViewDetails = { onCourseSelected(course.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CourseStatusCard(icon: ImageVector, title: String, count: Int, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = color.copy(alpha = 0.1f)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                color,
                                color.copy(alpha = 0.7f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.textPrimary
                )

                AnimatedContent(
                    targetState = count,
                    transitionSpec = {
                        (slideInVertically { height -> height } + fadeIn()).togetherWith(
                            slideOutVertically { height -> -height } + fadeOut())
                    }
                ) { targetCount ->
                    Text(
                        text = "Количество: $targetCount",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                color,
                                color.copy(alpha = 0.7f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun EmptyStateMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(28.dp),
                    spotColor = AppColors.primary.copy(alpha = 0.1f)
                )
                .padding(32.dp)
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
                imageVector = Icons.Outlined.SearchOff,
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .scale(scale),
                tint = AppColors.textSecondary.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = AppColors.textPrimary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Проверьте позже или обратитесь к администратору",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.textSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailableCourseItem(course: Course, onEnroll: () -> Unit, animationDelay: Int) {
    var expanded by remember { mutableStateOf(false) }
    var isHovered by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(animationDelay.toLong())
        expanded = true
    }

    AnimatedVisibility(
        visible = expanded,
        enter = fadeIn(animationSpec = tween(500)) +
                expandVertically(animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ))
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),
            colors = CardDefaults.elevatedCardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isHovered) 8.dp else 4.dp
            ),
            shape = RoundedCornerShape(24.dp),
            onClick = { isHovered = !isHovered }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(if (isHovered) 24.dp else 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            AppColors.secondary,
                                            AppColors.secondary.copy(alpha = 0.7f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.MenuBook,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = course.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.textPrimary
                            )
                            Text(
                                text = "ID: ${course.id}",
                                style = MaterialTheme.typography.bodySmall,
                                color = AppColors.textSecondary
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = isHovered,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(modifier = Modifier.padding(top = 20.dp)) {
                        Text(
                            text = "Описание курса",
                            style = MaterialTheme.typography.labelLarge,
                            color = AppColors.primary,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Этот курс предоставляет студентам возможность изучить основные концепции и практики в данной области.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppColors.textSecondary
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    FilledTonalButton(
                        onClick = onEnroll,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = AppColors.secondary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PersonAdd,
                            contentDescription = "Enroll",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Записаться",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnrolledCourseItem(
    course: Course,
    grade: Float?,
    animationDelay: Int,
    onViewDetails: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(animationDelay.toLong())
        expanded = true
    }

    AnimatedVisibility(
        visible = expanded,
        enter = fadeIn(animationSpec = tween(500)) +
                expandVertically(animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ))
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        AppColors.primary,
                                        AppColors.primary.copy(alpha = 0.7f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.School,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = course.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.textPrimary
                        )
                        Text(
                            text = "ID курса: ${course.id}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.textSecondary
                        )
                    }

                    // Progress indicator
                    val progress = grade?.div(5f) ?: 0f
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                if (grade != null)
                                    AppColors.primary.copy(alpha = 0.1f)
                                else
                                    AppColors.textSecondary.copy(alpha = 0.1f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.size(56.dp),
                            strokeWidth = 4.dp,
                            color = if (grade != null) AppColors.primary else AppColors.textSecondary
                        )

                        Text(
                            text = grade?.toString() ?: "-",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (grade != null) AppColors.primary else AppColors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                val gradientColors = if (grade != null) {
                    listOf(
                        AppColors.primary.copy(alpha = 0.1f),
                        AppColors.primary.copy(alpha = 0.05f)
                    )
                } else {
                    listOf(
                        AppColors.textSecondary.copy(alpha = 0.1f),
                        AppColors.textSecondary.copy(alpha = 0.05f)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(brush = Brush.horizontalGradient(gradientColors))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (grade != null) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = null,
                            tint = if (grade != null)
                                AppColors.primary
                            else
                                AppColors.textSecondary,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = if (grade != null) "Текущая оценка" else "Статус оценки",
                                style = MaterialTheme.typography.labelMedium,
                                color = AppColors.textSecondary
                            )

                            Text(
                                text = grade?.let { "Оценка: $it из 100" } ?: "Оценка отсутствует",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (grade != null) FontWeight.Medium else FontWeight.Normal,
                                color = if (grade != null)
                                    AppColors.primary
                                else
                                    AppColors.textSecondary
                            )
                        }
                    }
                }

                // Action buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { /* View details action */ },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(48.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    AppColors.primary.copy(alpha = 0.5f),
                                    AppColors.primary
                                )
                            )
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Visibility,
                            contentDescription = "View details",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Подробнее",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    FilledTonalButton(
                        onClick = onViewDetails,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = AppColors.primary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PlayArrow,
                            contentDescription = "Start learning",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Начать",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CourseDetails(
    viewModel: MainViewModel,
    courseId: Int,
    onBack: () -> Unit,
    onTestTaken: (Int) -> Unit
) {
    LaunchedEffect(courseId) {
        viewModel.loadMaterials(courseId)
        viewModel.loadTests(courseId) // Load tests for the course
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AppColors.backgroundTop,
                        AppColors.backgroundBottom
                    )
                )
            )
            .padding(24.dp)
    ) {
        // Back button and header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .shadow(4.dp, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Назад",
                    tint = AppColors.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Детали курса",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AppColors.textPrimary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Course title
        val course = viewModel.courses.value.find { it.id == courseId }
        if (course != null) {
            Text(
                text = course.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = AppColors.textPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "ID курса: ${course.id}",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Materials header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppColors.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    tint = AppColors.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Учебные материалы",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AppColors.textPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Existing materials
        if (viewModel.materials.value.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = AppColors.textSecondary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Материалы отсутствуют",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.textSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                itemsIndexed(viewModel.materials.value) { index, material ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(AppColors.primary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${index + 1}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Материал ${index + 1}",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = AppColors.textPrimary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = material.content,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AppColors.textSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Tests header
        Spacer(modifier = Modifier.height(32.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppColors.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = null,
                    tint = AppColors.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Тесты",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AppColors.textPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Displaying tests
        if (viewModel.tests.value.isEmpty()) {
            Text(
                text = "Тесты отсутствуют",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.textSecondary
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(viewModel.tests.value) { index, test ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = test.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = AppColors.textPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Fixed: Simplify the button click handler
                                // Исправленный обработчик в функции CourseDetails
                                OutlinedButton(
                                    onClick = {
                                        // Сначала загружаем вопросы теста
                                        viewModel.loadTestQuestions(test.id)
                                        // Передаем корректный testId на экран тестирования
                                        onTestTaken(test.id)
                                    },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text(
                                        text = "Перейти к тесту",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Color palette object to be added to your Color.kt file
object AppColors {
    val primary = Color(0xFF6C63FF)
    val secondary = Color(0xFF9C8FFF)
    val backgroundTop = Color(0xFFF8F9FF)
    val backgroundBottom = Color(0xFFEEF1FF)
    val cardBackground = Color(0xFFFFFFFF)
    val textPrimary = Color(0xFF2B2B2B)
    val textSecondary = Color(0xFF757575)
    val fieldBorder = Color(0xFFE0E0E0)
    val iconTint = Color(0xFF9E9E9E)
    val errorContainer = Color(0xFFFFEBEE)
    val error = Color(0xFFE53935)
    val onErrorContainer = Color(0xFFB71C1C)
}