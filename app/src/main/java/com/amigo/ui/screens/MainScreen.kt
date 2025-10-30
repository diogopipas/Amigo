package com.amigo.ui.screens

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.amigo.model.Meal
import com.amigo.ui.components.LoadingDialog
import com.amigo.ui.components.MacroProgressBar
import com.amigo.ui.components.MealCard
import com.amigo.utils.rememberImagePicker
import com.amigo.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    onNavigateToDetails: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val dailySummary by viewModel.dailySummary.collectAsState()
    val recentMeals by viewModel.recentMeals.collectAsState()
    
    var showImagePickerDialog by remember { mutableStateOf(false) }
    var showAnalysisDialog by remember { mutableStateOf(false) }

    val imagePicker = rememberImagePicker(
        onImageSelected = { uri ->
            viewModel.analyzeMeal(uri)
            showAnalysisDialog = true
            showImagePickerDialog = false
        },
        onError = { error ->
            viewModel.dismissError()
            // Show error snackbar here if needed
        }
    )

    Scaffold(
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
            // Header with motivational message
            Text(
                text = "Keep it up, Amigo! 💙",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Daily Summary Card
            DailySummaryCard(dailySummary)

            // Analysis Results Card (if available)
            if (uiState.analyzedNutrition != null && uiState.currentImageUri != null) {
                AnalysisResultCard(
                    imageUri = uiState.currentImageUri!!,
                    nutrition = uiState.analyzedNutrition!!,
                    onSave = {
                        viewModel.saveMeal()
                        showAnalysisDialog = false
                    },
                    onCancel = {
                        viewModel.clearAnalysis()
                        showAnalysisDialog = false
                    }
                )
            }

            // Recent Meals Section
            if (recentMeals.isNotEmpty()) {
                Text(
                    text = "Recent Meals",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(recentMeals) { meal ->
                        RecentMealItem(
                            meal = meal,
                            onClick = { onNavigateToDetails(meal.id) }
                        )
                    }
                }
            }

            // Empty State
            if (recentMeals.isEmpty() && dailySummary?.isEmpty() != false) {
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

    // Loading Dialog
    if (uiState.isAnalyzing) {
        LoadingDialog()
    }

    // Error Snackbar
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Error will be shown via snackbar host state if needed
        }
    }
}

@Composable
private fun DailySummaryCard(summary: com.amigo.model.DailySummary?) {
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
                // Calories
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Calories",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${summary.totalCalories} kcal",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Macros
                MacroProgressBar(
                    label = "Protein",
                    current = summary.totalProtein,
                    target = 120.0,
                    color = MaterialTheme.colorScheme.primary
                )
                MacroProgressBar(
                    label = "Carbs",
                    current = summary.totalCarbs,
                    target = 200.0,
                    color = MaterialTheme.colorScheme.secondary
                )
                MacroProgressBar(
                    label = "Fat",
                    current = summary.totalFat,
                    target = 65.0,
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
    onSave: () -> Unit,
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
                    onClick = onSave,
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
private fun RecentMealItem(meal: Meal, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = Uri.parse(meal.imageUri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "${meal.calories} kcal",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
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

