package com.example.coursemanager.ui.theme.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coursemanager.data.TestQuestion
import com.example.coursemanager.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestTakingScreen(
    viewModel: MainViewModel,
    testId: Int,
    onBackPressed: () -> Unit,
    onTestCompleted: () -> Unit
) {
    // Получаем текущий тест из ViewModel
    val currentTest = viewModel.tests.value.find { it.id == testId }

    // Если тест не найден, показываем сообщение об ошибке
    if (currentTest == null) {
        ErrorScreen(message = "Ошибка: Тест не найден", onBackPressed = onBackPressed)
        return
    }

    // Получаем вопросы из ViewModel
    val questions by remember { viewModel.currentTestQuestions }

    // Запрос вопросов при первом отображении экрана
    LaunchedEffect(testId) {
        viewModel.loadTestQuestions(testId)
    }

    // Если список вопросов ещё не загружен, показываем индикатор загрузки
    if (questions.isEmpty()) {
        LoadingScreen()
        return
    }

    // Проверка, проходил ли ученик уже тест
    val studentId = viewModel.currentUser?.id ?: 0
    val hasAlreadyTaken = remember { mutableStateOf(viewModel.hasStudentTakenTest(testId, studentId)) }
    val previousScore = remember { mutableStateOf(viewModel.getStudentResultForTest(testId, studentId)?.score ?: 0f) }

    if (hasAlreadyTaken.value) {
        // Если тест уже проходился, отображаем результат
        TestCompletedScreen(
            score = previousScore.value,
            onBackPressed = onBackPressed
        )
        return
    }

    // Храним текущий индекс вопроса и выбранные ответы
    val currentQuestionIndex = remember { mutableStateOf(0) }
    val answers = remember { mutableStateListOf<String?>().apply { repeat(questions.size) { add(null) } } }

    // Вычисляем прогресс прохождения теста
    val progress = (currentQuestionIndex.value + 1).toFloat() / questions.size
    val animatedProgress = animateFloatAsState(targetValue = progress, label = "progress")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentTest.title ?: "Прохождение теста",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Индикатор прогресса
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Вопрос ${currentQuestionIndex.value + 1} из ${questions.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { animatedProgress.value },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    strokeCap = StrokeCap.Round,
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            // Отображение текущего вопроса
            val currentIndex = currentQuestionIndex.value
            val currentQuestion = questions[currentIndex]

            QuestionCard(
                question = currentQuestion,
                selectedOption = answers[currentIndex],
                onOptionSelected = { option -> answers[currentIndex] = option }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Кнопки навигации
            NavigationButtons(
                currentIndex = currentIndex,
                totalQuestions = questions.size,
                hasAnswer = answers[currentIndex] != null,
                onPrevious = { currentQuestionIndex.value-- },
                onNext = { currentQuestionIndex.value++ },
                onSubmit = {
                    // Подсчитываем баллы
                    var score = 0
                    questions.forEachIndexed { index, question ->
                        if (answers[index] == question.correctOption) {
                            score++
                        }
                    }
                    // Вычисляем процент успешности
                    val scorePercentage = (score.toFloat() / questions.size) * 100

                    // Сохраняем результат через ViewModel
                    viewModel.submitTestResult(testId, studentId, scorePercentage)

                    // Сообщаем о завершении теста для правильной навигации
                    onTestCompleted()
                }
            )
        }
    }
}

@Composable
fun QuestionCard(
    question: TestQuestion,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = question.questionText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Формируем список вариантов ответа: пары "метка" – текст варианта
            val options = listOf(
                "A" to question.optionA,
                "B" to question.optionB,
                "C" to question.optionC,
                "D" to question.optionD
            )

            options.forEach { (optionLabel, optionText) ->
                val isSelected = selectedOption == optionLabel
                OptionItem(
                    optionLabel = optionLabel,
                    optionText = optionText,
                    isSelected = isSelected,
                    onOptionSelected = { onOptionSelected(optionLabel) }
                )
            }
        }
    }
}

@Composable
fun OptionItem(
    optionLabel: String,
    optionText: String,
    isSelected: Boolean,
    onOptionSelected: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    val textColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOptionSelected),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = if (isSelected) BorderStroke(0.dp, Color.Transparent) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text(
                        text = optionLabel,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = optionText,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun NavigationButtons(
    currentIndex: Int,
    totalQuestions: Int,
    hasAnswer: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSubmit: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (currentIndex > 0) {
            OutlinedButton(
                onClick = onPrevious,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Предыдущий")
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        if (currentIndex < totalQuestions - 1) {
            Button(
                onClick = { if (hasAnswer) onNext() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = hasAnswer
            ) {
                Text("Следующий")
            }
        } else {
            Button(
                onClick = { if (hasAnswer) onSubmit() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = hasAnswer
            ) {
                Text("Завершить тест")
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Загрузка вопросов...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ErrorScreen(message: String, onBackPressed: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onBackPressed,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Вернуться назад")
            }
        }
    }
}

@Composable
fun TestCompletedScreen(score: Float, onBackPressed: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(72.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Вы уже проходили этот тест!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Круговой индикатор результата
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { score / 100f },
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 12.dp,
                        color = when {
                            score >= 80f -> MaterialTheme.colorScheme.primary
                            score >= 60f -> Color(0xFF4CAF50) // Green
                            score >= 40f -> Color(0xFFFFC107) // Yellow
                            else -> Color(0xFFF44336) // Red
                        },
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Text(
                        text = "${score.toInt()}%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onBackPressed,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "Вернуться к курсу",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
