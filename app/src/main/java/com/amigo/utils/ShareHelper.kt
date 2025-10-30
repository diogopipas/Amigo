package com.amigo.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.amigo.model.DailySummary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Helper for sharing daily summary and meal information
 */
object ShareHelper {
    
    fun shareDailySummary(context: Context, summary: DailySummary) {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val dateString = dateFormat.format(Date(summary.date))
        
        val shareText = """
            📊 My Amigo Daily Summary - $dateString
            
            🔥 Calories: ${String.format(Locale.getDefault(), "%,d", summary.totalCalories)}
            🥩 Protein: ${String.format(Locale.getDefault(), "%.1f", summary.totalProtein)}g
            🍞 Carbs: ${String.format(Locale.getDefault(), "%.1f", summary.totalCarbs)}g
            🥑 Fat: ${String.format(Locale.getDefault(), "%.1f", summary.totalFat)}g
            
            Tracked with Amigo 💙
        """.trimIndent()

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share daily summary"))
    }

    fun shareMeal(context: Context, imageUri: String?, calories: Int, protein: Double, carbs: Double, fat: Double) {
        val shareText = """
            📊 My Meal - Amigo
            
            🔥 Calories: $calories
            🥩 Protein: ${String.format(Locale.getDefault(), "%.1f", protein)}g
            🍞 Carbs: ${String.format(Locale.getDefault(), "%.1f", carbs)}g
            🥑 Fat: ${String.format(Locale.getDefault(), "%.1f", fat)}g
            
            Tracked with Amigo 💙
        """.trimIndent()

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            
            // Attach image if available
            imageUri?.let { uri ->
                val imageUriObject = Uri.parse(uri)
                putExtra(Intent.EXTRA_STREAM, imageUriObject)
                type = "image/*"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share meal"))
    }
}

