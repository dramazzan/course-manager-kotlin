package com.example.coursemanager.ui.theme.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
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
import androidx.compose.ui.unit.sp
import com.example.coursemanager.data.User
import com.example.coursemanager.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherScreen(viewModel: MainViewModel, onLogout: () -> Unit, onCreateTest: (Int) -> Unit) {
    var selectedCourseId by remember { mutableStateOf<Int?>(null) }
    var materialContent by remember { mutableStateOf("") }
    val gradeInputs = remember { mutableStateMapOf<Int, String>() }
    var enrolledStudents by remember { mutableStateOf<List<User>>(emptyList()) }

    // Track active tab when course is selected
    var activeTabIndex by remember { mutableStateOf(0) }
    val courseTabs = listOf("Материалы", "Тесты", "Студенты") // Added "Tests" tab

    // Selected course title
    var selectedCourseTitle by remember { mutableStateOf("") }

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
                        if (selectedCourseId == null) {
                            Icon(
                                imageVector = Icons.Rounded.School,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                        Text(
                            text = if (selectedCourseId == null) "Портал преподавателя" else selectedCourseTitle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                navigationIcon = {
                    if (selectedCourseId != null) {
                        IconButton(
                            onClick = {
                                selectedCourseId = null
                                gradeInputs.clear()
                                activeTabIndex = 0
                            },
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Назад к курсам"
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
                            contentDescription = "Выйти"
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
            if (selectedCourseId == null) {
                // Courses List Screen
                CourseListView(
                    viewModel = viewModel,
                    onSelectCourse = { course ->
                        selectedCourseId = course.id
                        selectedCourseTitle = course.title
                        viewModel.loadMaterials(course.id)
                        // Load tests for the course
                        viewModel.loadTests(course.id)
                        enrolledStudents = viewModel.loadStudentsForCourse(course.id)
                        enrolledStudents.forEach { student ->
                            if (gradeInputs[student.id] == null) {
                                val currentGrade = viewModel.grades.value.find {
                                    it.studentId == student.id && it.courseId == course.id
                                }
                                gradeInputs[student.id] = currentGrade?.grade?.toString() ?: ""
                            }
                        }
                    }
                )
            } else {
                // Course Detail Screen with tabs and now includes test creation for selected course
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
                ) {
                    // Add course actions row with Create Test button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledTonalButton(
                            onClick = {
                                selectedCourseId?.let { courseId -> onCreateTest(courseId) }
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = AppColors.secondary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Создать тест",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Создать тест",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Tabs for Materials, Tests, and Students
                    TabRow(
                        selectedTabIndex = activeTabIndex,
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
                                    .tabIndicatorOffset(tabPositions[activeTabIndex])
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
                        courseTabs.forEachIndexed { index, title ->
                            val selected = activeTabIndex == index
                            Tab(
                                selected = selected,
                                onClick = { activeTabIndex = index },
                                text = {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selected) AppColors.primary else AppColors.textSecondary
                                    )
                                },
                                icon = {
                                    val icon = when (index) {
                                        0 -> Icons.Default.LibraryBooks
                                        1 -> Icons.Default.Assessment
                                        else -> Icons.Default.Group
                                    }
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

                    // Content based on selected tab
                    when (activeTabIndex) {
                        0 -> MaterialsTabContent(
                            viewModel = viewModel,
                            courseId = selectedCourseId!!,
                            materialContent = materialContent,
                            onMaterialContentChange = { materialContent = it }
                        )
                        1 -> TestsTabContent( // New tab for tests
                            viewModel = viewModel,
                            courseId = selectedCourseId!!
                        )
                        2 -> StudentsTabContent(
                            viewModel = viewModel,
                            courseId = selectedCourseId!!,
                            students = enrolledStudents,
                            gradeInputs = gradeInputs
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StudentsTabContent(
    viewModel: MainViewModel,
    courseId: Int,
    students: List<User>,
    gradeInputs: SnapshotStateMap<Int, String>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Students header
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
                                    AppColors.primary,
                                    AppColors.primary.copy(alpha = 0.7f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Список студентов",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.textPrimary
                    )

                    Text(
                        text = "Всего студентов: ${students.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.textSecondary
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
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
                    Text(
                        text = students.size.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Students list
        if (students.isEmpty()) {
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
                        imageVector = Icons.Outlined.PeopleAlt,
                        contentDescription = null,
                        modifier = Modifier
                            .size(72.dp)
                            .scale(scale),
                        tint = AppColors.textSecondary.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "На курс не записано ни одного студента",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppColors.textPrimary,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Студенты появятся здесь после записи на курс",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(students) { student ->
                    val studentIndex = students.indexOf(student)
                    StudentGradeCard(
                        student = student,
                        gradeInput = gradeInputs[student.id] ?: "",
                        onGradeChange = { newGrade ->
                            gradeInputs[student.id] = newGrade
                        },
                        onSaveGrade = {
                            val gradeText = gradeInputs[student.id] ?: ""
                            if (gradeText.isNotBlank()) {
                                try {
                                    val gradeValue = gradeText.toFloat()
                                    viewModel.assignGrade(courseId, student.id, gradeValue)
                                } catch (e: NumberFormatException) {
                                    // Handle invalid input
                                }
                            }
                        },
                        index = studentIndex
                    )
                }
            }

            // Optional: Add a summary or stats section at the bottom
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Assessment,
                            contentDescription = null,
                            tint = AppColors.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Всего студентов:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppColors.textPrimary
                        )
                    }
                    Text(
                        text = "${students.size}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.primary
                    )
                }
            }
        }
    }
}

@Composable
fun CourseListView(viewModel: MainViewModel, onSelectCourse: (com.example.coursemanager.data.Course) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with stats
        TeacherStatsCard(courseCount = viewModel.courses.value.size)

        Spacer(modifier = Modifier.height(24.dp))

        // Section title
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
                                    AppColors.secondary,
                                    AppColors.secondary.copy(alpha = 0.7f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Ваши курсы",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.textPrimary
                    )

                    Text(
                        text = "Всего курсов: ${viewModel.courses.value.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.textSecondary
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
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
                    Text(
                        text = viewModel.courses.value.size.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.courses.value.isEmpty()) {
            // Empty state
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
                        imageVector = Icons.Default.LibraryBooks,
                        contentDescription = null,
                        modifier = Modifier
                            .size(72.dp)
                            .scale(scale),
                        tint = AppColors.textSecondary.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "У вас еще нет курсов",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppColors.textPrimary,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Курсы появятся здесь после их создания",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Course list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(viewModel.courses.value) { index, course ->
                    CourseCard(
                        course = course,
                        index = index,
                        onClick = { onSelectCourse(course) }
                    )
                }
            }
        }
    }
}

@Composable
fun TeacherStatsCard(courseCount: Int) {
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
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Панель преподавателя",
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
                StatisticItem(
                    icon = Icons.Default.LibraryBooks,
                    title = "Курсы",
                    value = courseCount.toString(),
                    color = AppColors.primary
                )

                // You can add more stats here if needed
                StatisticItem(
                    icon = Icons.Default.Group,
                    title = "Студенты",
                    value = "Активные",
                    color = AppColors.secondary
                )
            }
        }
    }
}

@Composable
fun StatisticItem(icon: ImageVector, title: String, value: String, color: Color) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseCard(course: com.example.coursemanager.data.Course, index: Int, onClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 100L)
        expanded = true
    }

    AnimatedVisibility(
        visible = expanded,
        enter = fadeIn(animationSpec = tween(300)) +
                slideInVertically(animationSpec = tween(300)) { it / 2 }
    ) {
        ElevatedCard(
            onClick = onClick,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
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
                // Course icon
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
                    Text(
                        text = course.title.take(1).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
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
                        text = "ID: ${course.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.textSecondary
                    )
                }

                // Interactive indicator
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AppColors.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Подробнее",
                        tint = AppColors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MaterialsTabContent(
    viewModel: MainViewModel,
    courseId: Int,
    materialContent: String,
    onMaterialContentChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Materials header
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
                                    AppColors.primary,
                                    AppColors.primary.copy(alpha = 0.7f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Учебные материалы",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.textPrimary
                    )

                    Text(
                        text = "Количество: ${viewModel.materials.value.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.textSecondary
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
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
                    Text(
                        text = viewModel.materials.value.size.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Existing materials
        if (viewModel.materials.value.isEmpty()) {
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
                        imageVector = Icons.Outlined.MenuBook,
                        contentDescription = null,
                        modifier = Modifier
                            .size(72.dp)
                            .scale(scale),
                        tint = AppColors.textSecondary.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Материалы отсутствуют",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppColors.textPrimary,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Добавьте первый материал с помощью формы ниже",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.textSecondary,
                        textAlign = TextAlign.Center
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

        Spacer(modifier = Modifier.height(16.dp))

        // Add new material section
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = AppColors.primary.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AppColors.secondary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = AppColors.secondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Добавить новый материал",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.textPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = materialContent,
                    onValueChange = onMaterialContentChange,
                    label = { Text("Содержание материала") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = AppColors.primary,
                        unfocusedIndicatorColor = AppColors.fieldBorder,
                        focusedLabelColor = AppColors.primary,
                        unfocusedLabelColor = AppColors.textSecondary,
                        cursorColor = AppColors.primary
                    ),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                FilledTonalButton(
                    onClick = {
                        if (materialContent.isNotBlank()) {
                            viewModel.addMaterial(courseId, materialContent)
                            onMaterialContentChange("")
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .height(48.dp),
                    enabled = materialContent.isNotBlank(),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = AppColors.secondary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Добавить",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Добавить материал",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentGradeCard(
    student: User,
    gradeInput: String,
    onGradeChange: (String) -> Unit,
    onSaveGrade: () -> Unit,
    index: Int
) {
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 100L)
        expanded = true
    }

    AnimatedVisibility(
        visible = expanded,
        enter = fadeIn(animationSpec = tween(300)) +
                slideInVertically(animationSpec = tween(300)) { it / 2 }
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Student avatar/initials
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        AppColors.secondary,
                                        AppColors.primary
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = student.name.take(1).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = student.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.textPrimary
                        )
                        Text(
                            text = student.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = gradeInput,
                        onValueChange = onGradeChange,
                        label = { Text("Оценка") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = AppColors.primary,
                            unfocusedIndicatorColor = AppColors.fieldBorder,
                            focusedLabelColor = AppColors.primary,
                            unfocusedLabelColor = AppColors.textSecondary,
                            cursorColor = AppColors.primary
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )

                    FilledTonalButton(
                        onClick = onSaveGrade,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = AppColors.primary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Сохранить оценку",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Сохранить",
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
fun TestsTabContent(
    viewModel: MainViewModel,
    courseId: Int
) {
    // State for delete confirmation dialog
    var showDeleteDialog by remember { mutableStateOf(false) }
    var testToDelete by remember { mutableStateOf<Int?>(null) }
    var testTitleToDelete by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Tests header
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
                                    AppColors.primary,
                                    AppColors.primary.copy(alpha = 0.7f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Assessment,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Управление тестами",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.textPrimary
                    )

                    Text(
                        text = "Количество: ${viewModel.tests.value.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.textSecondary
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
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
                    Text(
                        text = viewModel.tests.value.size.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Displaying tests
        if (viewModel.tests.value.isEmpty()) {
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
                        imageVector = Icons.Outlined.Quiz,
                        contentDescription = null,
                        modifier = Modifier
                            .size(72.dp)
                            .scale(scale),
                        tint = AppColors.textSecondary.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Тесты отсутствуют",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppColors.textPrimary,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Создайте первый тест с помощью кнопки \"Создать тест\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                itemsIndexed(viewModel.tests.value) { index, test ->
                    // Animation for item appearance
                    var expanded by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(index * 100L)
                        expanded = true
                    }

                    AnimatedVisibility(
                        visible = expanded,
                        enter = fadeIn(animationSpec = tween(300)) +
                                slideInVertically(animationSpec = tween(300)) { it / 2 },
                        exit = fadeOut(animationSpec = tween(300)) +
                                slideOutVertically(animationSpec = tween(300)) { it / 2 }
                    ) {
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
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
                                                    .size(40.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(AppColors.primary.copy(alpha = 0.1f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Assignment,
                                                    contentDescription = null,
                                                    tint = AppColors.primary,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column {
                                                Text(
                                                    text = test.title,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = AppColors.textPrimary
                                                )

                                                Text(
                                                    text = "ID: ${test.id}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = AppColors.textSecondary
                                                )
                                            }
                                        }

                                        IconButton(
                                            onClick = {
                                                testToDelete = test.id
                                                testTitleToDelete = test.title
                                                showDeleteDialog = true
                                            },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(AppColors.error.copy(alpha = 0.1f))
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Удалить тест",
                                                tint = AppColors.error,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Statistics about the test
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                color = AppColors.backgroundTop.copy(alpha = 0.5f),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        TestStatCard(
                                            icon = Icons.Default.QuestionAnswer,
                                            title = "Вопросов",
                                            value = "-" // Mock data, should come from viewModel
                                        )

                                        Divider(
                                            modifier = Modifier
                                                .height(36.dp)
                                                .width(1.dp),
                                            color = AppColors.fieldBorder
                                        )

                                        TestStatCard(
                                            icon = Icons.Default.People,
                                            title = "Прошли",
                                            value = "-" // Mock data, should come from viewModel
                                        )

                                        Divider(
                                            modifier = Modifier
                                                .height(36.dp)
                                                .width(1.dp),
                                            color = AppColors.fieldBorder
                                        )

                                        TestStatCard(
                                            icon = Icons.Default.Star,
                                            title = "Сред. балл",
                                            value = "-" // Mock data, should come from viewModel
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

//                                    // Action buttons
//                                    Row(
//                                        modifier = Modifier.fillMaxWidth(),
//                                        horizontalArrangement = Arrangement.End,
//                                        verticalAlignment = Alignment.CenterVertically
//                                    ) {
//                                        OutlinedButton(
//                                            onClick = { /* Handle edit test */ },
//                                            shape = RoundedCornerShape(8.dp),
//                                            colors = ButtonDefaults.outlinedButtonColors(
//                                                contentColor = AppColors.primary
//                                            )
//                                        ) {
//                                            Icon(
//                                                imageVector = Icons.Default.Edit,
//                                                contentDescription = "Редактировать",
//                                                modifier = Modifier.size(16.dp)
//                                            )
//                                            Spacer(modifier = Modifier.width(4.dp))
//                                            Text(
//                                                text = "Редактировать",
//                                                style = MaterialTheme.typography.bodySmall
//                                            )
//                                        }
//
//                                        Spacer(modifier = Modifier.width(8.dp))
//
//                                        Button(
//                                            onClick = { /* Handle preview test */ },
//                                            shape = RoundedCornerShape(8.dp),
//                                            colors = ButtonDefaults.buttonColors(
//                                                containerColor = AppColors.primary
//                                            )
//                                        ) {
//                                            Icon(
//                                                imageVector = Icons.Default.Visibility,
//                                                contentDescription = "Просмотр",
//                                                modifier = Modifier.size(16.dp)
//                                            )
//                                            Spacer(modifier = Modifier.width(4.dp))
//                                            Text(
//                                                text = "Просмотреть",
//                                                style = MaterialTheme.typography.bodySmall
//                                            )
//                                        }
//                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Confirmation dialog for test deletion
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    testToDelete = null
                },
                title = {
                    Text(
                        "Подтверждение удаления",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        "Вы действительно хотите удалить тест \"$testTitleToDelete\"? " +
                                "Это действие нельзя отменить.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Delete the test
                            testToDelete?.let { testId ->
                                viewModel.deleteTest(testId)
                                // Reload tests after deletion
                                viewModel.loadTests(courseId)
                            }
                            showDeleteDialog = false
                            testToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.error
                        )
                    ) {
                        Text("Удалить")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            testToDelete = null
                        }
                    ) {
                        Text("Отмена")
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
fun TestStatCard(
    icon: ImageVector,
    title: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AppColors.primary,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = AppColors.textSecondary
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = AppColors.textPrimary
        )
    }
}