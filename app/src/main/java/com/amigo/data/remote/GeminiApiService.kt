package com.amigo.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.amigo.model.NutritionData
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.math.max

/**
 * Service for interacting with Gemini API for meal image analysis
 */
class GeminiApiService(private val context: Context) {

    private fun getApiKey(): String {
        // Read from local.properties
        val properties = java.util.Properties()
        try {
            val propertiesFile = java.io.File(context.filesDir.parentFile, "../local.properties")
            if (propertiesFile.exists()) {
                propertiesFile.inputStream().use { properties.load(it) }
            }
        } catch (e: Exception) {
            // Fallback: try to read from assets or use environment variable
            e.printStackTrace()
        }
        
        return properties.getProperty("GEMINI_API_KEY", "").ifEmpty {
            // Try environment variable as fallback
            System.getenv("GEMINI_API_KEY") ?: ""
        }
    }

    /**
     * Compress image to max 1024px dimension while maintaining aspect ratio
     */
    private suspend fun compressImage(bitmap: Bitmap): Bitmap = withContext(Dispatchers.IO) {
        val maxDimension = 1024
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxDimension && height <= maxDimension) {
            return@withContext bitmap
        }

        val scale = maxDimension.toFloat() / max(width, height)
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()

        Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    /**
     * Convert Uri to Bitmap
     */
    private suspend fun uriToBitmap(uri: Uri): Bitmap = withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        } ?: throw IllegalArgumentException("Unable to load image from URI")
    }

    /**
     * Convert Bitmap to ByteArray (JPEG format, quality 85%)
     */
    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        return outputStream.toByteArray()
    }

    /**
     * Analyze meal image using Gemini API and extract nutritional information
     */
    suspend fun analyzeMealImage(imageUri: Uri): Result<NutritionData> = withContext(Dispatchers.IO) {
        try {
            val apiKey = getApiKey()
            if (apiKey.isEmpty()) {
                return@withContext Result.failure(
                    Exception("Gemini API key not found. Please add GEMINI_API_KEY to local.properties")
                )
            }

            // Load and compress image
            val originalBitmap = uriToBitmap(imageUri)
            val compressedBitmap = compressImage(originalBitmap)
            val imageBytes = bitmapToByteArray(compressedBitmap)

            // Initialize Gemini model
            val generativeModel = GenerativeModel(
                modelName = "gemini-pro-vision",
                apiKey = apiKey
            )

            // Create prompt for nutritional analysis
            val prompt = """
                Analyze this meal image and provide nutritional estimates in JSON format.
                Return ONLY a valid JSON object with the following structure (no markdown, no code blocks, just pure JSON):
                {
                    "calories": <integer>,
                    "protein": <number in grams>,
                    "carbs": <number in grams>,
                    "fat": <number in grams>
                }
                
                Be realistic and accurate in your estimates. If you cannot identify the food clearly, provide your best estimate.
            """.trimIndent()

            // Prepare image content
            val imageContent = content {
                image(originalBitmap)
                text(prompt)
            }

            // Generate response
            val response = generativeModel.generateContent(imageContent)
            val responseText = response.text ?: throw Exception("Empty response from Gemini API")

            // Parse JSON response
            val nutritionData = parseNutritionResponse(responseText)

            Result.success(nutritionData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Parse Gemini API response into NutritionData
     * Handles both JSON-only responses and markdown-wrapped JSON
     */
    private fun parseNutritionResponse(response: String): NutritionData {
        // Clean response: remove markdown code blocks if present
        var cleanedResponse = response.trim()
        if (cleanedResponse.startsWith("```json")) {
            cleanedResponse = cleanedResponse.removePrefix("```json").trim()
        }
        if (cleanedResponse.startsWith("```")) {
            cleanedResponse = cleanedResponse.removePrefix("```").trim()
        }
        if (cleanedResponse.endsWith("```")) {
            cleanedResponse = cleanedResponse.removeSuffix("```").trim()
        }

        // Try to extract JSON object
        val jsonStart = cleanedResponse.indexOf('{')
        val jsonEnd = cleanedResponse.lastIndexOf('}')
        
        if (jsonStart == -1 || jsonEnd == -1 || jsonEnd <= jsonStart) {
            throw Exception("Invalid JSON response format: $response")
        }

        val jsonString = cleanedResponse.substring(jsonStart, jsonEnd + 1)
        
        // Parse JSON using Android's JSONObject
        val jsonObject = org.json.JSONObject(jsonString)
        
        return NutritionData(
            calories = jsonObject.getInt("calories"),
            protein = jsonObject.getDouble("protein"),
            carbs = jsonObject.getDouble("carbs"),
            fat = jsonObject.getDouble("fat")
        )
    }
}

