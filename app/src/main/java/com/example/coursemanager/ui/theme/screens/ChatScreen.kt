package com.example.coursemanager.ui.theme.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun ChatScreen(apiKey: String) {
    var message by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf("Привет, как я могу помочь?", "Здравствуйте!") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Чат", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Список сообщений
        Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
            messages.forEach { msg ->
                Text(msg, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Ввод сообщения
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BasicTextField(
                value = message,
                onValueChange = { message = it },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (message.isNotBlank()) {
                            messages.add(message)
                            message = ""
                        }
                    }
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .border(1.dp, MaterialTheme.colorScheme.primary)
                    .padding(8.dp)
            )

            IconButton(
                onClick = {
                    if (message.isNotBlank()) {
                        messages.add(message)
                        message = ""
                    }
                }
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send Message")
            }
        }
    }
}
