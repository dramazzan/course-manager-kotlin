package com.example.coursemanager

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class GeminiAPIClient(private val apiKey: String) {

    private val client = OkHttpClient()
    private val JSON = "application/json; charset=utf-8".toMediaType()

    suspend fun getAssistantResponse(userInput: String): String {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey"

        val jsonBody = """
            {
              "contents": [
                {
                  "parts": [
                    {
                      "text": "$userInput"
                    }
                  ]
                }
              ]
            }
        """.trimIndent()

        val requestBody = jsonBody.toRequestBody(JSON)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    val jsonResponse = JSONObject(responseBody)

                    if (jsonResponse.has("candidates") &&
                        jsonResponse.getJSONArray("candidates").length() > 0) {

                        val candidate = jsonResponse.getJSONArray("candidates").getJSONObject(0)
                        if (candidate.has("content") &&
                            candidate.getJSONObject("content").has("parts") &&
                            candidate.getJSONObject("content").getJSONArray("parts").length() > 0) {

                            return@withContext candidate.getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text")
                        }
                    }

                    "Ошибка: Неожиданный формат ответа API"
                } else {
                    "Ошибка ${response.code}: ${responseBody ?: "Нет тела ответа"}"
                }
            } catch (e: IOException) {
                "Ошибка сети: ${e.message}"
            } catch (e: Exception) {
                "Ошибка обработки: ${e.message}"
            }
        }
    }

    private fun logResponse(response: String) {
        println("API ответ: $response")
    }
}