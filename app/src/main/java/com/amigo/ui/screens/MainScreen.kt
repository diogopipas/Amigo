package com.amigo.ui.screens

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.amigo.data.MascotMessages
import com.amigo.ui.components.AmigoMascotInline
import com.amigo.ui.components.AmigoMascotOverlay
import com.amigo.ui.components.MacroProgressBar
import com.amigo.ui.components.MealCard
import com.amigo.utils.rememberImagePicker
import com.amigo.viewmodel.MainViewModel
import com.amigo.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel(),
    onNavigateToDetails: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val dailySummary by viewModel.dailySummary.collectAsState()
    val todayMeals by viewModel.todayMeals.collectAsState()
    
    val calorieGoal by settingsViewModel.calorieGoal.collectAsState()
    val proteinGoal by settingsViewModel.proteinGoal.collectAsState()
    val carbsGoal by settingsViewModel.carbsGoal.collectAsState()
    val fatGoal by settingsViewModel.fatGoal.collectAsState()
    
    var showImagePickerDialog by remember { mutableStateOf(false) }
    var showAnalysisDialog by remember { mutableStateOf(false) }

    val imagePicker = rememberImagePicker(
        onImageSelected = { uri ->
            viewModel.analyzeMeal(uri)
            showAnalysisDialog = true
            showImagePickerDialog = false
        },
        onError = { _ ->
            viewModel.dismissError()
            // Show error snackbar here if needed
        }
    )

    val snackbarHostState = remember { SnackbarHostState() }
    var savedJustNowTip by remember { mutableStateOf<String?>(null) }
    
    // Remember the motivational message so it doesn't change when returning to the screen
    val motivationalMessage = rememberSaveable {
        MascotMessages.randomAny()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showImagePickerDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add meal"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with mascot and rotating message
            AmigoMascotInline(
                message = motivationalMessage
            )

            // Daily Summary Card
            DailySummaryCard(
                summary = dailySummary,
                calorieGoal = calorieGoal,
                proteinGoal = proteinGoal.toDouble(),
                carbsGoal = carbsGoal.toDouble(),
                fatGoal = fatGoal.toDouble()
            )

            // Analysis Results Card (if available)
            if (uiState.analyzedNutrition != null && uiState.currentImageUri != null) {
                AnalysisResultCard(
                    imageUri = uiState.currentImageUri!!,
                    nutrition = uiState.analyzedNutrition!!,
                    onSave = { quantity ->
                        viewModel.saveMeal(quantity)
                        savedJustNowTip = MascotMessages.afterSaveMessage()
                        showAnalysisDialog = false
                    },
                    onCancel = {
                        viewModel.clearAnalysis()
                        showAnalysisDialog = false
                    }
                )
            }

            // Today's Meals Section
            if (todayMeals.isNotEmpty()) {
                Text(
                    text = "Today's Meals",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    todayMeals.forEach { meal ->
                        MealCard(
                            meal = meal,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onNavigateToDetails(meal.id) }
                        )
                    }
                }
            }

            // Empty State
            if (todayMeals.isEmpty() && dailySummary?.isEmpty() != false) {
                EmptyState()
            }
        }
    }

    // Image Picker Dialog
    if (showImagePickerDialog) {
        ImagePickerDialog(
            onDismiss = { showImagePickerDialog = false },
            onCameraClick = {
                imagePicker.takePicture()
            },
            onGalleryClick = {
                imagePicker.pickFromGallery()
            }
        )
    }

    // Analysis overlay: Amigo speaks while analyzing
    if (uiState.isAnalyzing) {
        AmigoMascotOverlay(
            message = MascotMessages.analyzingMessage(),
            onDismiss = { /* modal, no-op while analyzing */ }
        )
    }

    // Error Snackbar
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Error will be shown via snackbar host state if needed
        }
    }

    // Show encouragement snackbar after saving a meal
    if (savedJustNowTip != null) {
        LaunchedEffect(savedJustNowTip) {
            snackbarHostState.showSnackbar(savedJustNowTip!!)
            savedJustNowTip = null
        }
    }
}

@Composable
private fun DailySummaryCard(
    summary: com.amigo.model.DailySummary?,
    calorieGoal: Int,
    proteinGoal: Double,
    carbsGoal: Double,
    fatGoal: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Today's Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (summary?.isEmpty() == false) {
                // Calories with progress
                MacroProgressBar(
                    label = "Calories",
                    current = summary.totalCalories.toDouble(),
                    target = calorieGoal.toDouble(),
                    color = MaterialTheme.colorScheme.primary,
                    unit = "kcal"
                )

                // Macros
                MacroProgressBar(
                    label = "Protein",
                    current = summary.totalProtein,
                    target = proteinGoal,
                    color = MaterialTheme.colorScheme.primary
                )
                MacroProgressBar(
                    label = "Carbs",
                    current = summary.totalCarbs,
                    target = carbsGoal,
                    color = MaterialTheme.colorScheme.secondary
                )
                MacroProgressBar(
                    label = "Fat",
                    current = summary.totalFat,
                    target = fatGoal,
                    color = MaterialTheme.colorScheme.tertiary
                )
            } else {
                Text(
                    text = "No meals logged today. Take a photo to get started!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun AnalysisResultCard(
    imageUri: Uri,
    nutrition: com.amigo.model.NutritionData,
    onSave: (Double) -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Analysis Result",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            AsyncImage(
                model = imageUri,
                contentDescription = "Analyzed meal",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NutritionItem("Calories", "${nutrition.calories} kcal")
                NutritionItem("Protein", "${String.format(java.util.Locale.getDefault(), "%.1f", nutrition.protein)}g")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NutritionItem("Carbs", "${String.format(java.util.Locale.getDefault(), "%.1f", nutrition.carbs)}g")
                NutritionItem("Fat", "${String.format(java.util.Locale.getDefault(), "%.1f", nutrition.fat)}g")
            }

            var quantityText by remember { mutableStateOf("1.0") }
            val quantity = quantityText.toDoubleOrNull()?.coerceAtLeast(0.1) ?: 1.0

            Text(
                text = "Quantity",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = {
                    val current = quantityText.toDoubleOrNull() ?: 1.0
                    val next = (current - 0.5).coerceAtLeast(0.1)
                    quantityText = String.format(java.util.Locale.getDefault(), "%.1f", next)
                }) {
                    Text("-")
                }
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = {
                        val cleaned = it.replace(',', '.')
                        if (cleaned.isEmpty() || cleaned == "." || cleaned.toDoubleOrNull() != null) {
                            quantityText = cleaned
                        }
                    },
                    label = { Text("Servings") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedButton(onClick = {
                    val current = quantityText.toDoubleOrNull() ?: 1.0
                    val next = current + 0.5
                    quantityText = String.format(java.util.Locale.getDefault(), "%.1f", next)
                }) {
                    Text("+")
                }
            }

            val previewCalories = (nutrition.calories * quantity).toInt()
            val previewProtein = nutrition.protein * quantity
            val previewCarbs = nutrition.carbs * quantity
            val previewFat = nutrition.fat * quantity

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NutritionItem("Calories", "$previewCalories kcal")
                NutritionItem("Protein", "${String.format(java.util.Locale.getDefault(), "%.1f", previewProtein)}g")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NutritionItem("Carbs", "${String.format(java.util.Locale.getDefault(), "%.1f", previewCarbs)}g")
                NutritionItem("Fat", "${String.format(java.util.Locale.getDefault(), "%.1f", previewFat)}g")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = { onSave(quantity) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save Meal")
                }
            }
        }
    }
}

@Composable
private fun NutritionItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = androidx.compose.material.icons.Icons.Default.PhotoCamera,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Text(
            text = "No meals yet",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = "Tap the + button to add your first meal!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun ImagePickerDialog(
    onDismiss: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Meal") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(
                    onClick = {
                        onCameraClick()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.PhotoCamera,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Take Photo")
                }
                TextButton(
                    onClick = {
                        onGalleryClick()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.PhotoLibrary,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Choose from Gallery")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

