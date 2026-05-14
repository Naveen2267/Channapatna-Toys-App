package com.example.channapatnatoys.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenRouterService @Inject constructor(
    private val httpClient: OkHttpClient
) {
    companion object {
        private const val BASE_URL = "https://openrouter.ai/api/v1/chat/completions"
        // Model: google/gemma-4-27b-it:free (free tier on OpenRouter)
        private const val MODEL = "openrouter/free"
    }

    /**
     * Sends a prompt to OpenRouter and returns the response text.
     * Returns null if the request fails.
     */
    suspend fun generateContent(apiKey: String, prompt: String): String? =
        withContext(Dispatchers.IO) {
            try {
                if (apiKey.isBlank()) {
                    Log.e("OpenRouter", "API Key is empty! Check local.properties")
                    return@withContext null
                }

                val requestBody = JSONObject().apply {
                    put("model", MODEL)
                    put("messages", JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "user")
                            put("content", prompt)
                        })
                    })
                }.toString()

                Log.d("OpenRouter", "Request Body: $requestBody")

                val request = Request.Builder()
                    .url(BASE_URL)
                    .addHeader("Authorization", "Bearer $apiKey")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("HTTP-Referer", "https://channapatnatoys.app")
                    .addHeader("X-Title", "Channapatna Toys")
                    .post(requestBody.toRequestBody("application/json".toMediaType()))
                    .build()

                val response = httpClient.newCall(request).execute()
                val responseBodyStr = response.body?.string() ?: return@withContext null

                if (!response.isSuccessful) {
                    Log.e("OpenRouter", "Error Response: ${response.code} - $responseBodyStr")
                    return@withContext null
                }

                Log.d("OpenRouter", "Response: $responseBodyStr")

                // Parse the OpenAI-compatible response
                val json = JSONObject(responseBodyStr)
                json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")

            } catch (e: Exception) {
                Log.e("OpenRouter", "Exception: ${e.message}", e)
                null
            }
        }
}
