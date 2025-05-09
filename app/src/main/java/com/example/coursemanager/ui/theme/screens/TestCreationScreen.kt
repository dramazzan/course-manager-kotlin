package com.example.coursemanager.ui.theme.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.coursemanager.data.TestQuestion
import com.example.coursemanager.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestCreationScreen(
    viewModel: MainViewModel,
    courseId: Int,
    onBackPressed: () -> Unit,
    onTestCreated: () -> Unit
) {
    var testTitle by remember { mutableStateOf("") }
    var testDescription by remember { mutableStateOf("") }

    val questions = remember { mutableStateListOf<QuestionData>() }

    LaunchedEffect(Unit) {
        if (questions.isEmpty()) {
            questions.add(createEmptyQuestion())
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
                title = { Text("Создание теста") },
                navigationIcon = {
                    IconButton(
                        onClick = onBackPressed,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Test information section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = AppColors.primary.copy(alpha = 0.1f)
                    ),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Header with icon
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
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
                            Icon(
                                imageVector = Icons.Default.Quiz,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = "Информация о тесте",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = testTitle,
                        onValueChange = { testTitle = it },
                        label = { Text("Название теста") },
                        modifier = Modifier.fillMaxWidth(),
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

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = testDescription,
                        onValueChange = { testDescription = it },
                        label = { Text("Описание теста") },
                        modifier = Modifier.fillMaxWidth(),
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
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Questions section header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Вопросы (${questions.size}/10)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.textPrimary
                )

                if (questions.size < 10) {
                    FilledTonalButton(
                        onClick = { questions.add(createEmptyQuestion()) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = AppColors.secondary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Добавить вопрос"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Добавить вопрос")
                    }
                }
            }

            // Questions list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(questions) { index, questionData ->
                    QuestionCard(
                        questionData = questionData,
                        questionNumber = index + 1,
                        onQuestionUpdated = { updatedQuestion ->
                            questions[index] = updatedQuestion
                        },
                        onDeleteQuestion = {
                            if (questions.size > 1) {
                                questions.removeAt(index)
                            }
                        }
                    )
                }

                // Space at the bottom for better UX
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            // Create button
            Button(
                onClick = {
                    if (isTestValid(testTitle, questions)) {
                        val testQuestions = questions.mapIndexed { index, questionData ->
                            TestQuestion(
                                testId = 0, // Will be updated in ViewModel
                                questionText = questionData.questionText,
                                optionA = questionData.optionA,
                                optionB = questionData.optionB,
                                optionC = questionData.optionC,
                                optionD = questionData.optionD,
                                correctOption = when (questionData.correctOption) {
                                    0 -> "A"
                                    1 -> "B"
                                    2 -> "C"
                                    3 -> "D"
                                    else -> "A"
                                },
                                orderNumber = index + 1
                            )
                        }

                        viewModel.createTest(courseId, testTitle, testDescription, testQuestions)
                        onTestCreated()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.primary,
                    contentColor = Color.White
                ),
                enabled = isTestValid(testTitle, questions),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Создать тест",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun QuestionCard(
    questionData: QuestionData,
    questionNumber: Int,
    onQuestionUpdated: (QuestionData) -> Unit,
    onDeleteQuestion: () -> Unit
) {
    var expanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = AppColors.primary.copy(alpha = 0.1f)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Question header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AppColors.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = questionNumber.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Вопрос ${questionNumber}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.textPrimary
                    )
                }

                Row {
                    IconButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AppColors.fieldBorder.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (expanded) "Свернуть" else "Развернуть",
                            tint = AppColors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onDeleteQuestion,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.Red.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Удалить вопрос",
                            tint = Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(200))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    // Question text
                    OutlinedTextField(
                        value = questionData.questionText,
                        onValueChange = {
                            onQuestionUpdated(questionData.copy(questionText = it))
                        },
                        label = { Text("Текст вопроса") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = AppColors.primary,
                            unfocusedIndicatorColor = AppColors.fieldBorder,
                            focusedLabelColor = AppColors.primary,
                            unfocusedLabelColor = AppColors.textSecondary,
                            cursorColor = AppColors.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Options
                    Text(
                        text = "Варианты ответов",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Option A
                    OptionTextField(
                        value = questionData.optionA,
                        onValueChange = { onQuestionUpdated(questionData.copy(optionA = it)) },
                        label = "Вариант A",
                        isSelected = questionData.correctOption == 0,
                        onSelectOption = { onQuestionUpdated(questionData.copy(correctOption = 0)) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Option B
                    OptionTextField(
                        value = questionData.optionB,
                        onValueChange = { onQuestionUpdated(questionData.copy(optionB = it)) },
                        label = "Вариант B",
                        isSelected = questionData.correctOption == 1,
                        onSelectOption = { onQuestionUpdated(questionData.copy(correctOption = 1)) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Option C
                    OptionTextField(
                        value = questionData.optionC,
                        onValueChange = { onQuestionUpdated(questionData.copy(optionC = it)) },
                        label = "Вариант C",
                        isSelected = questionData.correctOption == 2,
                        onSelectOption = { onQuestionUpdated(questionData.copy(correctOption = 2)) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Option D
                    OptionTextField(
                        value = questionData.optionD,
                        onValueChange = { onQuestionUpdated(questionData.copy(optionD = it)) },
                        label = "Вариант D",
                        isSelected = questionData.correctOption == 3,
                        onSelectOption = { onQuestionUpdated(questionData.copy(correctOption = 3)) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Correct answer note
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = AppColors.secondary,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Отметьте радиокнопкой правильный вариант ответа",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.textSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OptionTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isSelected: Boolean,
    onSelectOption: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelectOption,
            colors = RadioButtonDefaults.colors(
                selectedColor = AppColors.primary,
                unselectedColor = AppColors.textSecondary
            )
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = if (isSelected) AppColors.primary else AppColors.fieldBorder,
                unfocusedIndicatorColor = if (isSelected) AppColors.primary.copy(alpha = 0.5f) else AppColors.fieldBorder,
                focusedLabelColor = if (isSelected) AppColors.primary else AppColors.textSecondary,
                unfocusedLabelColor = if (isSelected) AppColors.primary.copy(alpha = 0.7f) else AppColors.textSecondary,
                cursorColor = AppColors.primary
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

// Data class for question state management
data class QuestionData(
    val questionText: String = "",
    val optionA: String = "",
    val optionB: String = "",
    val optionC: String = "",
    val optionD: String = "",
    val correctOption: Int = 0 // 0 = A, 1 = B, 2 = C, 3 = D
)

// Helper function to create an empty question
fun createEmptyQuestion() = QuestionData(
    questionText = "",
    optionA = "",
    optionB = "",
    optionC = "",
    optionD = "",
    correctOption = 0
)

// Validation function
fun isTestValid(title: String, questions: List<QuestionData>): Boolean {
    if (title.isBlank() || questions.isEmpty()) return false

    return questions.all { question ->
        question.questionText.isNotBlank() &&
                question.optionA.isNotBlank() &&
                question.optionB.isNotBlank() &&
                question.optionC.isNotBlank() &&
                question.optionD.isNotBlank()
    }
}