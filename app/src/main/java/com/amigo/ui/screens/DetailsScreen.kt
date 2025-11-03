package com.amigo.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.amigo.utils.ShareHelper
import com.amigo.viewmodel.DetailsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    mealId: Int,
    viewModel: DetailsViewModel = viewModel(),
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val meal by viewModel.meal.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPortionDialog by remember { mutableStateOf(false) }

    LaunchedEffect(mealId) {
        viewModel.loadMeal(mealId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meal Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    meal?.let {
                        IconButton(onClick = { showPortionDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Portions"
                            )
                        }
                        IconButton(
                            onClick = {
                                ShareHelper.shareMeal(
                                    context,
                                    it.imageUri,
                                    it.calories,
                                    it.protein,
                                    it.carbs,
                                    it.fat
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share"
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (meal == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Meal not found")
            }
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Meal Image
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(Uri.parse(meal!!.imageUri))
                        .crossfade(true)
                        .memoryCacheKey("meal-image-" + meal!!.id)
                        .diskCacheKey("meal-image-" + meal!!.id)
                        .build(),
                    contentDescription = "Meal image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop
                )

                // Meal Details Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Date/Time
                        val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
                        Text(
                            text = dateFormat.format(Date(meal!!.timestamp)),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        // Calories
                        Column {
                            Text(
                                text = "Calories",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "${meal!!.calories} kcal",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Divider()

                        // Macros
                        Text(
                            text = "Macronutrients",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        MacroDetailItem(
                            label = "Protein",
                            value = meal!!.protein,
                            unit = "g",
                            color = MaterialTheme.colorScheme.primary
                        )
                        MacroDetailItem(
                            label = "Carbohydrates",
                            value = meal!!.carbs,
                            unit = "g",
                            color = MaterialTheme.colorScheme.secondary
                        )
                        MacroDetailItem(
                            label = "Fat",
                            value = meal!!.fat,
                            unit = "g",
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Meal") },
            text = { Text("Are you sure you want to delete this meal?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        meal?.let {
                            viewModel.deleteMeal(it.id)
                            onNavigateBack()
                        }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Portion Edit Dialog
    if (showPortionDialog && meal != null) {
        var quantityText by remember { mutableStateOf("1.0") }
        val quantity = quantityText.toDoubleOrNull()?.coerceAtLeast(0.1) ?: 1.0

        val previewCalories = (meal!!.calories * quantity).toInt()
        val previewProtein = meal!!.protein * quantity
        val previewCarbs = meal!!.carbs * quantity
        val previewFat = meal!!.fat * quantity

        AlertDialog(
            onDismissRequest = { showPortionDialog = false },
            title = { Text("Edit Portions") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Adjust servings to scale this meal's nutrition.")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(onClick = {
                            val current = quantityText.toDoubleOrNull() ?: 1.0
                            val next = (current - 0.5).coerceAtLeast(0.1)
                            quantityText = String.format(Locale.getDefault(), "%.1f", next)
                        }) { Text("-") }
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
                            quantityText = String.format(Locale.getDefault(), "%.1f", next)
                        }) { Text("+") }
                    }

                    Divider()
                    Text("Preview")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text("Calories: $previewCalories kcal")
                        Text("Protein: ${String.format(Locale.getDefault(), "%.1f", previewProtein)}g")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text("Carbs: ${String.format(Locale.getDefault(), "%.1f", previewCarbs)}g")
                        Text("Fat: ${String.format(Locale.getDefault(), "%.1f", previewFat)}g")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.scaleMealPortions(meal!!.id, quantity)
                    showPortionDialog = false
                }) {
                    Text("Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPortionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun MacroDetailItem(
    label: String,
    value: Double,
    unit: String,
    color: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "${String.format(Locale.getDefault(), "%.1f", value)} $unit",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

