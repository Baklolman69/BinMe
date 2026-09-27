package com.tensormind.binme.data.remote

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.tensormind.binme.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

data class ChatCompletionRequest(
    @SerializedName("model") val model: String = "qwen/qwen3.8-27b",
    @SerializedName("messages") val messages: List<GroqMessage>,
    @SerializedName("temperature") val temperature: Double = 0.7
)

data class GroqMessage(
    @SerializedName("role") val role: String, // "system", "user", "assistant"
    @SerializedName("content") val content: String
)

data class ChatCompletionResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("choices") val choices: List<Choice>?,
    @SerializedName("error") val error: ErrorResponse? = null
)

data class ErrorResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("type") val type: String?
)

data class Choice(
    @SerializedName("message") val message: GroqMessage?
)

data class VisionImageUrl(
    @SerializedName("url") val url: String
)

data class VisionContentPart(
    @SerializedName("type") val type: String, // "text" or "image_url"
    @SerializedName("text") val text: String? = null,
    @SerializedName("image_url") val imageUrl: VisionImageUrl? = null
)

data class VisionMessage(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: Any // String for system message, List<VisionContentPart> for user prompt with image
)

data class VisionResponseFormat(
    @SerializedName("type") val type: String = "json_object"
)

data class VisionCompletionRequest(
    @SerializedName("model") val model: String = "qwen/qwen3.8-27b",
    @SerializedName("messages") val messages: List<VisionMessage>,
    @SerializedName("max_tokens") val maxTokens: Int = 500,
    @SerializedName("temperature") val temperature: Double = 0.2,
    @SerializedName("response_format") val responseFormat: VisionResponseFormat? = VisionResponseFormat("json_object")
)

interface GroqApiService {
    @POST("v1/chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: ChatCompletionRequest
    ): Response<ChatCompletionResponse>

    @POST("v1/chat/completions")
    suspend fun getVisionChatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: VisionCompletionRequest
    ): Response<ChatCompletionResponse>

    companion object {
        private const val BASE_URL = "https://api.groq.com/openai/"

        fun create(): GroqApiService {
            val loggingInterceptor = HttpLoggingInterceptor { message ->
                Log.d("GroqAPI", message)
            }.apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val original = chain.request()
                    val request = original.newBuilder()
                        .header("User-Agent", "BinMe-Android/1.0")
                        .build()
                    chain.proceed(request)
                }
                .addInterceptor(loggingInterceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GroqApiService::class.java)
        }
    }
}

class GroqRepository(
    private val apiService: GroqApiService = GroqApiService.create()
) {
    // Default fallback API key set to empty string for privacy & security in public repositories.
    // Configure your GROQ_API_KEY in local.properties
    private val defaultApiKey = ""

    private val supportedModels = listOf(
        "qwen/qwen3.8-27b",
        "qwen-2.5-coder-32b"
    )

    private val systemPrompt = GroqMessage(
        role = "system",
        content = """
            You are BinME, an expert AI waste sorting assistant for Lake Oswego & Clackamas County, OR.
            ${com.tensormind.binme.data.LocalRecyclingDatabase.SYSTEM_PROMPT_KNOWLEDGE}

            Help users classify items into Recyclable, Compostable, or Trash based on official Lake Oswego municipal regulations.
            Always include citations to official sources (Lake Oswego Sustainability: https://www.ci.oswego.or.us/sustainability or Clackamas County Recycling: https://www.clackamas.us/recycling).
            Keep responses structured, friendly, concise, and highly informative.
            When answering, include the primary classification clearly: [Compost], [Recyclable], or [Trash].
        """.trimIndent()
    )

    suspend fun sendMessage(userQuery: String, history: List<GroqMessage> = emptyList()): String {
        val configKey = try { BuildConfig.GROQ_API_KEY } catch (_: Exception) { "" }
        val apiKey = if (configKey.isNotBlank() && configKey != "\"\"" && configKey.startsWith("gsk_")) {
            configKey
        } else {
            defaultApiKey
        }

        Log.d("GroqRepository", "Using API key: ${apiKey.take(10)}...${apiKey.takeLast(4)}")
        val authHeader = "Bearer $apiKey"

        val messagesList = mutableListOf<GroqMessage>()
        messagesList.add(systemPrompt)
        messagesList.addAll(history)
        messagesList.add(GroqMessage(role = "user", content = userQuery))

        for (model in supportedModels) {
            try {
                Log.d("GroqRepository", "Attempting request with model: $model")
                val response = apiService.getChatCompletion(
                    authorization = authHeader,
                    request = ChatCompletionRequest(
                        model = model,
                        messages = messagesList
                    )
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    val content = body?.choices?.firstOrNull()?.message?.content
                    if (!content.isNullOrBlank()) {
                        Log.d("GroqRepository", "Successfully received AI response from model: $model")
                        return content
                    } else {
                        Log.w("GroqRepository", "Model $model returned empty content. Body: $body")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("GroqRepository", "HTTP ${response.code()} for model $model: $errorBody")
                }
            } catch (e: Exception) {
                Log.e("GroqRepository", "Exception calling model $model: ${e.javaClass.simpleName} - ${e.message}", e)
            }
        }

        Log.w("GroqRepository", "All Qwen AI models failed. Using local fallback response.")
        return getFallbackResponse(userQuery)
    }

    private fun getFallbackResponse(query: String): String {
        val lower = query.lowercase()
        return when {
            "pizza box" in lower || "compost" in lower -> {
                "[Compost]\n\nYes — you can compost this in Lake Oswego!\n\nThis is a greasy pizza box, which is typically compostable in most cities, including Lake Oswego. The food residue and grease make it unsuitable for recycling, but it can go in the compost bin."
            }
            "coffee cup" in lower || "plastic cup" in lower -> {
                "[Recyclable]\n\nA plastic coffee cup is usually recyclable, but it depends on your city's rules. In Lake Oswego, most plastic cups (especially #1 PET) are recyclable — just make sure it's empty and clean. If it has a lid, the lid is often recyclable too (but check if it's a different material)."
            }
            "bottle" in lower || "plastic" in lower -> {
                "[Recyclable]\n\nPlastic bottles (PET #1 and HDPE #2) are widely recyclable in curbside programs! Just empty, rinse, and place the cap back on before putting it in your green recycling bin."
            }
            else -> {
                "[Trash]\n\nIn Lake Oswego and most cities, items should be empty, clean, and dry before recycling. If an item is heavily soiled with food residue, it should go into the compost or trash bin."
            }
        }
    }
}


