package com.tensormind.binme.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.tensormind.binme.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream

data class ActionPillData(
    val title: String,
    val subtitle: String,
    val iconType: String // "water", "cap", "bin"
)

data class ScanResultData(
    val category: String, // "Recycle", "Compost", "Trash"
    val confidence: String, // "94% confidence"
    val itemName: String, // "Plastic Bottle (PET #1)"
    val description: String, // "This is typically recyclable because it's made from PET plastic..."
    val locationGuidanceTitle: String, // "Yes, you can recycle this in Lake Oswego!"
    val locationGuidanceSubtitle: String, // "Place it in your mixed recycling bin..."
    val actionPills: List<ActionPillData>,
    val imageUri: String? = null,
    val verifiedSourceTitle: String = "Lake Oswego Sustainability",
    val verifiedSourceUrl: String = "https://www.ci.oswego.or.us/sustainability",
    val classificationRationale: String = "Classified according to official Lake Oswego waste sorting regulations.",
    val isUncertain: Boolean = false,
    val uncertaintyReason: String? = null,
    val nearbyLocations: List<com.tensormind.binme.data.DisposalLocationData> = emptyList(),
    val barcode: String? = null,
    val co2SavedKg: Double = 0.12,
    val landfillDivertedKg: Double = 0.05
)

class GroqVisionRepository(
    private val apiService: GroqApiService = GroqApiService.create()
) {
    // Default fallback API key set to empty string for security
    private val defaultApiKey = ""

    suspend fun analyzeImage(
        context: Context?,
        bitmap: Bitmap? = null,
        imageUriStr: String? = null,
        itemHint: String? = null
    ): ScanResultData = withContext(Dispatchers.IO) {
        val apiKey = try {
            if (BuildConfig.GROQ_API_KEY.isNotBlank() && BuildConfig.GROQ_API_KEY != "\"\"") {
                BuildConfig.GROQ_API_KEY
            } else {
                defaultApiKey
            }
        } catch (_: Exception) {
            defaultApiKey
        }

        val authHeader = "Bearer $apiKey"

        // Convert Bitmap or Uri to Base64 Data URL quickly
        val base64DataUrl = when {
            bitmap != null -> bitmapToBase64DataUrl(bitmap, maxDim = 512)
            !imageUriStr.isNullOrBlank() && context != null -> uriToBase64DataUrl(context, imageUriStr)
            else -> null
        }

        // If we have image data, call Groq Multimodal Vision API
        if (!base64DataUrl.isNullOrBlank()) {
            try {
                Log.d("GroqVisionRepository", "Calling Groq Multimodal Vision API with verified Lake Oswego database...")
                val systemPrompt = VisionMessage(
                    role = "system",
                    content = """
                        You are BinME AI, an expert AI vision waste sorting & recycling classifier for Lake Oswego & Clackamas County, OR.
                        ${com.tensormind.binme.data.LocalRecyclingDatabase.SYSTEM_PROMPT_KNOWLEDGE}

                        Analyze the provided image carefully:
                        - Identify the primary item. If dirty, mixed-material, broken, or partially visible, detail how to separate components.
                        - Classify it into "Recycle", "Compost", or "Trash".
                        - If the image is too blurry, dark, ambiguous, or item is unknown, set "isUncertain": true and "confidence": "Uncertain (45%)".
                        
                        Return ONLY a JSON object with this exact schema:
                        {
                          "itemName": "Specific waste item name (e.g., Plastic Water Bottle (PET #1))",
                          "category": "Recycle" or "Compost" or "Trash",
                          "confidence": "e.g. 96% confidence",
                          "description": "Clear explanation of why this item belongs in this waste category.",
                          "locationGuidanceTitle": "City guidance title for Lake Oswego, OR (e.g., Yes, recycle in Lake Oswego mixed bin!)",
                          "locationGuidanceSubtitle": "Clear action instruction for curbside bins.",
                          "classificationRationale": "Specific material & sorting rule explanation",
                          "verifiedSourceTitle": "Lake Oswego Sustainability",
                          "verifiedSourceUrl": "https://www.ci.oswego.or.us/sustainability",
                          "isUncertain": false or true,
                          "uncertaintyReason": "Reason if uncertain (or empty if confident)",
                          "actionPills": [
                            { "title": "Action 1", "subtitle": "Detail 1", "iconType": "water" },
                            { "title": "Action 2", "subtitle": "Detail 2", "iconType": "cap" },
                            { "title": "Action 3", "subtitle": "Detail 3", "iconType": "bin" }
                          ]
                        }
                    """.trimIndent()
                )

                val userPrompt = VisionMessage(
                    role = "user",
                    content = listOf(
                        VisionContentPart(type = "text", text = "Identify and classify this waste item for Lake Oswego, OR."),
                        VisionContentPart(type = "image_url", imageUrl = VisionImageUrl(url = base64DataUrl))
                    )
                )

                val response = apiService.getVisionChatCompletion(
                    authorization = authHeader,
                    request = VisionCompletionRequest(
                        model = "qwen/qwen3.8-27b",
                        messages = listOf(systemPrompt, userPrompt),
                        maxTokens = 400,
                        temperature = 0.1,
                        responseFormat = VisionResponseFormat("json_object")
                    )
                )

                if (response.isSuccessful) {
                    val jsonText = response.body()?.choices?.firstOrNull()?.message?.content
                    Log.d("GroqVisionRepository", "Vision AI Raw Output: $jsonText")
                    if (!jsonText.isNullOrBlank()) {
                        val parsed = parseVisionJsonResponse(jsonText, imageUriStr)
                        if (parsed != null) {
                            com.tensormind.binme.data.ScanHistoryRepository.addScanResult(parsed)
                            return@withContext parsed
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("GroqVisionRepository", "Vision API call failed, falling back to offline database", e)
            }
        }

        // Fast offline fallback if image API failed or no image was provided
        val offlineResult = com.tensormind.binme.data.LocalRecyclingDatabase.lookupOfflineRule(itemHint ?: "Plastic Bottle")
        val finalResult = offlineResult.copy(imageUri = imageUriStr)
        com.tensormind.binme.data.ScanHistoryRepository.addScanResult(finalResult)
        return@withContext finalResult
    }

    suspend fun analyzeObject(
        itemHint: String = "Plastic Bottle",
        imageUri: String? = null
    ): ScanResultData = withContext(Dispatchers.IO) {
        val apiKey = try {
            if (BuildConfig.GROQ_API_KEY.isNotBlank() && BuildConfig.GROQ_API_KEY != "\"\"") {
                BuildConfig.GROQ_API_KEY
            } else {
                defaultApiKey
            }
        } catch (_: Exception) {
            defaultApiKey
        }

        val authHeader = "Bearer $apiKey"

        val systemPrompt = GroqMessage(
            role = "system",
            content = """
                You are BinME, an expert AI vision waste sorting classifier. 
                Analyze the item '$itemHint' and classify whether it is Recyclable, Compostable, or Trash.
                Provide structured details on material type, recycling confidence percentage, and curbside guidance for Lake Oswego, OR.
            """.trimIndent()
        )

        val messagesList = listOf(systemPrompt, GroqMessage(role = "user", content = "Classify this waste item: $itemHint"))

        val resultData = try {
            val response = apiService.getChatCompletion(
                authorization = authHeader,
                request = ChatCompletionRequest(
                    model = "qwen/qwen3.8-27b",
                    messages = messagesList
                )
            )

            if (response.isSuccessful) {
                val rawText = response.body()?.choices?.firstOrNull()?.message?.content ?: ""
                parseAiVisionResponse(rawText, itemHint, imageUri)
            } else {
                getDefaultScanResult(itemHint, imageUri)
            }
        } catch (e: Exception) {
            getDefaultScanResult(itemHint, imageUri)
        }

        com.tensormind.binme.data.ScanHistoryRepository.addScanResult(resultData)
        return@withContext resultData
    }

    private fun parseVisionJsonResponse(jsonStr: String, imageUri: String?): ScanResultData? {
        return try {
            val json = JSONObject(jsonStr)
            val itemName = json.optString("itemName", "Scanned Waste Item")
            val category = json.optString("category", "Recycle")
            val confidence = json.optString("confidence", "95% confidence")
            val description = json.optString("description", "Analyzed by BinME AI Vision Classifier.")
            val locTitle = json.optString("locationGuidanceTitle", "Guidance for Lake Oswego, OR")
            val locSub = json.optString("locationGuidanceSubtitle", "Place in proper curbside sorting bin.")
            val rationale = json.optString("classificationRationale", "Classified according to official Lake Oswego sorting rules.")
            val verifiedTitle = json.optString("verifiedSourceTitle", "Lake Oswego Sustainability")
            val verifiedUrl = json.optString("verifiedSourceUrl", "https://www.ci.oswego.or.us/sustainability")
            val isUncertain = json.optBoolean("isUncertain", false) || confidence.contains("uncertain", ignoreCase = true)
            val uncertaintyReason = json.optString("uncertaintyReason", if (isUncertain) "The image may be unclear or contain mixed materials." else null)

            val pillsList = mutableListOf<ActionPillData>()
            val pillsArr = json.optJSONArray("actionPills")
            if (pillsArr != null) {
                for (i in 0 until pillsArr.length()) {
                    val p = pillsArr.getJSONObject(i)
                    pillsList.add(
                        ActionPillData(
                            title = p.optString("title", "Action"),
                            subtitle = p.optString("subtitle", "Detail"),
                            iconType = p.optString("iconType", "bin")
                        )
                    )
                }
            }

            if (pillsList.isEmpty()) {
                pillsList.add(ActionPillData("Clean & Prep", "Remove food or liquid residues.", "water"))
                pillsList.add(ActionPillData("Separate Cap", "Check cap material if applicable.", "cap"))
                pillsList.add(ActionPillData("Place in Bin", "Sort into designated container.", "bin"))
            }

            val nearby = com.tensormind.binme.data.LocalRecyclingDatabase.NEARBY_LOCATIONS

            ScanResultData(
                category = category,
                confidence = confidence,
                itemName = itemName,
                description = description,
                locationGuidanceTitle = locTitle,
                locationGuidanceSubtitle = locSub,
                actionPills = pillsList,
                imageUri = imageUri,
                verifiedSourceTitle = verifiedTitle,
                verifiedSourceUrl = verifiedUrl,
                classificationRationale = rationale,
                isUncertain = isUncertain,
                uncertaintyReason = uncertaintyReason,
                nearbyLocations = nearby
            )
        } catch (e: Exception) {
            Log.e("GroqVisionRepository", "Failed parsing vision JSON: $jsonStr", e)
            null
        }
    }

    private fun bitmapToBase64DataUrl(bitmap: Bitmap, maxDim: Int = 768): String {
        val width = bitmap.width
        val height = bitmap.height
        val scale = if (width > maxDim || height > maxDim) {
            maxDim.toFloat() / Math.max(width, height)
        } else 1.0f

        val scaledW = (width * scale).toInt().coerceAtLeast(1)
        val scaledH = (height * scale).toInt().coerceAtLeast(1)
        val scaledBitmap = if (scale < 1.0f) Bitmap.createScaledBitmap(bitmap, scaledW, scaledH, true) else bitmap

        val out = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
        val bytes = out.toByteArray()
        val b64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
        return "data:image/jpeg;base64,$b64"
    }

    private fun uriToBase64DataUrl(context: Context, imageUriStr: String): String? {
        return try {
            val uri = Uri.parse(imageUriStr)
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            if (bitmap != null) bitmapToBase64DataUrl(bitmap) else null
        } catch (e: Exception) {
            Log.e("GroqVisionRepository", "Failed converting Uri to Base64 data URL", e)
            null
        }
    }

    private fun parseAiVisionResponse(
        rawText: String,
        itemHint: String,
        imageUri: String? = null
    ): ScanResultData {
        val lowerText = rawText.lowercase()
        val isCompost = "compost" in lowerText || "apple" in lowerText.lowercase() || "food" in lowerText.lowercase()
        val isTrash = "trash" in lowerText || "landfill" in lowerText

        val category = when {
            isCompost -> "Compost"
            isTrash -> "Trash"
            else -> "Recycle"
        }

        val locTitle = when (category) {
            "Compost" -> "Yes, you can compost this in Lake Oswego!"
            "Trash" -> "No, this goes into regular trash in Lake Oswego."
            else -> "Yes, you can recycle this in Lake Oswego!"
        }

        val locSub = when (category) {
            "Compost" -> "Place in your curbside compost bin. Ensure no plastic stickers or non-compostable wrappers are attached."
            "Trash" -> "Bag securely and place in your black trash container for weekly pickup."
            else -> "Place it in your mixed recycling bin. Make sure it's empty and cap is removed (if possible)."
        }

        val pills = when (category) {
            "Compost" -> listOf(
                ActionPillData("Scrape food", "Remove non-compostable packaging", "water"),
                ActionPillData("No plastics", "Check for stickers", "cap"),
                ActionPillData("Place in", "green compost bin", "bin")
            )
            "Trash" -> listOf(
                ActionPillData("Wipe clean", "Prevent bad odors in bin", "water"),
                ActionPillData("Bag it", "Seal securely before bin", "cap"),
                ActionPillData("Place in", "trash bin", "bin")
            )
            else -> listOf(
                ActionPillData("Rinse it", "Remove leftover liquid or food.", "water"),
                ActionPillData("Remove cap", "(if possible) ℹ", "cap"),
                ActionPillData("Place in", "recycling bin", "bin")
            )
        }

        return ScanResultData(
            category = category,
            confidence = "94% confidence",
            itemName = if (itemHint.isNotBlank()) itemHint else "Waste Item",
            description = if (rawText.isNotBlank()) {
                rawText.take(180) + "..."
            } else {
                "This item was analyzed by BinME AI waste classification service for Lake Oswego."
            },
            locationGuidanceTitle = locTitle,
            locationGuidanceSubtitle = locSub,
            actionPills = pills,
            imageUri = imageUri
        )
    }

    fun getDefaultScanResult(
        itemHint: String = "Plastic Bottle (PET #1)",
        imageUri: String? = null
    ): ScanResultData {
        return ScanResultData(
            category = "Recycle",
            confidence = "94% confidence",
            itemName = if (itemHint.isNotBlank() && itemHint != "Plastic Bottle") itemHint else "Plastic Bottle (PET #1)",
            description = "This is typically recyclable because it's made from PET plastic. Empty and rinse it before placing it in the recycling bin.",
            locationGuidanceTitle = "Yes, you can recycle this in Lake Oswego!",
            locationGuidanceSubtitle = "Place it in your mixed recycling bin. Make sure it's empty and cap is removed (if possible).",
            actionPills = listOf(
                ActionPillData("Rinse it", "Remove leftover liquid or food.", "water"),
                ActionPillData("Remove cap", "(if possible) ℹ", "cap"),
                ActionPillData("Place in", "recycling bin", "bin")
            ),
            imageUri = imageUri
        )
    }
}
