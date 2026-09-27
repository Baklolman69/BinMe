package com.tensormind.binme.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CloudflareVisionRepository {
    private val groqVisionRepo = GroqVisionRepository()

    /**
     * Performs AI vision classification on the scanned waste item image using Groq Multimodal Vision (Qwen3.8-27B).
     */
    suspend fun classifyImageWithCloudflare(
        context: Context,
        imageUriStr: String?,
        customWorkerUrl: String? = null,
        bitmap: Bitmap? = null
    ): ScanResultData = withContext(Dispatchers.IO) {
        Log.d("CloudflareVisionRepo", "Analyzing waste photo via Groq Multimodal Vision AI...")
        return@withContext groqVisionRepo.analyzeImage(
            context = context,
            bitmap = bitmap,
            imageUriStr = imageUriStr
        )
    }
}
