package com.example.coursemanager

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ChatMessage(
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Date = Date()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantScreen(apiKey: String) {
    var userInput by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var chatMessages by remember { mutableStateOf(listOf<ChatMessage>()) }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    val geminiAPIClient = remember { GeminiAPIClient(apiKey) }

    val primaryColor = MaterialTheme.colorScheme.primary
    val userBubbleColor = Color(0xFF2A96FA)
    val assistantBubbleColor = MaterialTheme.colorScheme.surfaceVariant
    val userTextColor = Color.White
    val assistantTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(primaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "AI",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Виртуальный помощник",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Готов помочь вам с вопросами",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = scrollState,
                    reverseLayout = true,
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(chatMessages.reversed()) { message ->
                        val alignment = if (message.isFromUser) Alignment.End else Alignment.Start
                        val bubbleColor = if (message.isFromUser) userBubbleColor else assistantBubbleColor
                        val textColor = if (message.isFromUser) userTextColor else assistantTextColor

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalAlignment = alignment
                        ) {
                            Card(
                                modifier = Modifier
                                    .widthIn(max = 340.dp)
                                    .padding(horizontal = 8.dp),
                                shape = RoundedCornerShape(
                                    topStart = if (message.isFromUser) 16.dp else 4.dp,
                                    topEnd = if (message.isFromUser) 4.dp else 16.dp,
                                    bottomStart = 16.dp,
                                    bottomEnd = 16.dp
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = bubbleColor
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Text(
                                        text = message.content,
                                        color = textColor,
                                        fontSize = 16.sp
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Timestamp
                                    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                                    Text(
                                        text = formatter.format(message.timestamp),
                                        fontSize = 12.sp,
                                        color = textColor.copy(alpha = 0.7f),
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }

                this@Column.AnimatedVisibility(
                    visible = isLoading,
                    enter = fadeIn(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300)),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Помощник печатает...", fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Область ввода
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Исправлено: использование правильных стилей для TextField в Material3
                    TextField(
                        value = userInput,
                        onValueChange = { userInput = it },
                        placeholder = {
                            Text(
                                "Введите вопрос...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        maxLines = 4
                    )

                    FloatingActionButton(
                        onClick = {
                            if (userInput.isNotBlank() && !isLoading) {
                                val userMessage = userInput.trim()
                                userInput = ""

                                coroutineScope.launch {
                                    // Добавляем сообщение пользователя
                                    chatMessages = chatMessages + ChatMessage(
                                        content = userMessage,
                                        isFromUser = true
                                    )

                                    // Прокручиваем к последнему сообщению
                                    scrollState.animateScrollToItem(0)

                                    // Получаем ответ от API
                                    isLoading = true
                                    val response = geminiAPIClient.getAssistantResponse(userMessage)
                                    isLoading = false

                                    // Добавляем ответ ассистента
                                    chatMessages = chatMessages + ChatMessage(
                                        content = response,
                                        isFromUser = false
                                    )

                                    // Снова прокручиваем к последнему сообщению
                                    scrollState.animateScrollToItem(0)
                                }
                            }
                        },
                        modifier = Modifier.size(48.dp),
                        containerColor = primaryColor,
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Отправить",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}