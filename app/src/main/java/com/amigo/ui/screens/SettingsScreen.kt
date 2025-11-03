package com.amigo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.amigo.viewmodel.SettingsViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val apiKey by viewModel.apiKey.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val calorieGoal by viewModel.calorieGoal.collectAsState()
    val proteinGoal by viewModel.proteinGoal.collectAsState()
    val carbsGoal by viewModel.carbsGoal.collectAsState()
    val fatGoal by viewModel.fatGoal.collectAsState()
    val weightGoal by viewModel.weightGoal.collectAsState()
    val currentWeight by viewModel.currentWeight.collectAsState()
    
    var apiKeyInput by remember { mutableStateOf(apiKey) }
    
    var calorieGoalInput by remember { mutableStateOf(calorieGoal.toString()) }
    var proteinGoalInput by remember { mutableStateOf(String.format(Locale.getDefault(), "%.1f", proteinGoal)) }
    var carbsGoalInput by remember { mutableStateOf(String.format(Locale.getDefault(), "%.1f", carbsGoal)) }
    var fatGoalInput by remember { mutableStateOf(String.format(Locale.getDefault(), "%.1f", fatGoal)) }
    var weightGoalInput by remember { mutableStateOf(if (weightGoal > 0) String.format(Locale.getDefault(), "%.1f", weightGoal) else "") }
    var currentWeightInput by remember { mutableStateOf(if (currentWeight > 0) String.format(Locale.getDefault(), "%.1f", currentWeight) else "") }
    
    // Update input fields when goals change from DataStore
    LaunchedEffect(calorieGoal) {
        calorieGoalInput = calorieGoal.toString()
    }
    LaunchedEffect(proteinGoal) {
        proteinGoalInput = String.format(Locale.getDefault(), "%.1f", proteinGoal)
    }
    LaunchedEffect(carbsGoal) {
        carbsGoalInput = String.format(Locale.getDefault(), "%.1f", carbsGoal)
    }
    LaunchedEffect(fatGoal) {
        fatGoalInput = String.format(Locale.getDefault(), "%.1f", fatGoal)
    }
    LaunchedEffect(weightGoal) {
        weightGoalInput = if (weightGoal > 0) String.format(Locale.getDefault(), "%.1f", weightGoal) else ""
    }
    LaunchedEffect(currentWeight) {
        currentWeightInput = if (currentWeight > 0) String.format(Locale.getDefault(), "%.1f", currentWeight) else ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // API Key Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Gemini API Key",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "Enter your Gemini API key to enable meal analysis. Get your key from https://aistudio.google.com/app/apikey",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        label = { Text("API Key") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("Enter your API key") }
                    )

                    Button(
                        onClick = {
                            viewModel.saveApiKey(apiKeyInput)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save API Key")
                    }
                }
            }

            // Theme Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Appearance",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Theme",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = themeMode == "light",
                                onClick = { viewModel.setThemeMode("light") },
                                label = { Text("Light") }
                            )
                            FilterChip(
                                selected = themeMode == "dark",
                                onClick = { viewModel.setThemeMode("dark") },
                                label = { Text("Dark") }
                            )
                            FilterChip(
                                selected = themeMode == "system",
                                onClick = { viewModel.setThemeMode("system") },
                                label = { Text("System") }
                            )
                        }
                    }
                }
            }

            // Nutrition Goals Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Daily Nutrition Goals",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "Set your daily targets for calories and macros to track your progress.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    OutlinedTextField(
                        value = calorieGoalInput,
                        onValueChange = { 
                            if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                calorieGoalInput = it
                            }
                        },
                        label = { Text("Daily Calories (kcal)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = proteinGoalInput,
                            onValueChange = { 
                                val cleaned = it.replace(',', '.')
                                if (cleaned.isEmpty() || cleaned == "." || cleaned.toDoubleOrNull() != null) {
                                    proteinGoalInput = cleaned
                                }
                            },
                            label = { Text("Protein (g)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            )
                        )
                        OutlinedTextField(
                            value = carbsGoalInput,
                            onValueChange = { 
                                val cleaned = it.replace(',', '.')
                                if (cleaned.isEmpty() || cleaned == "." || cleaned.toDoubleOrNull() != null) {
                                    carbsGoalInput = cleaned
                                }
                            },
                            label = { Text("Carbs (g)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            )
                        )
                        OutlinedTextField(
                            value = fatGoalInput,
                            onValueChange = { 
                                val cleaned = it.replace(',', '.')
                                if (cleaned.isEmpty() || cleaned == "." || cleaned.toDoubleOrNull() != null) {
                                    fatGoalInput = cleaned
                                }
                            },
                            label = { Text("Fat (g)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            )
                        )
                    }

                    Button(
                        onClick = {
                            val calories = calorieGoalInput.toIntOrNull() ?: 2000
                            val protein = proteinGoalInput.toFloatOrNull() ?: 120.0f
                            val carbs = carbsGoalInput.toFloatOrNull() ?: 200.0f
                            val fat = fatGoalInput.toFloatOrNull() ?: 65.0f
                            viewModel.setNutritionGoals(calories, protein, carbs, fat)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Goals")
                    }
                }
            }

            // Weight Tracking Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Weight Tracking",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "Set your weight goal and register your current weight to track your progress.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    OutlinedTextField(
                        value = weightGoalInput,
                        onValueChange = { 
                            val cleaned = it.replace(',', '.')
                            if (cleaned.isEmpty() || cleaned == "." || cleaned.toFloatOrNull() != null) {
                                weightGoalInput = cleaned
                            }
                        },
                        label = { Text("Weight Goal (kg)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        placeholder = { Text("Enter your target weight") }
                    )

                    OutlinedTextField(
                        value = currentWeightInput,
                        onValueChange = { 
                            val cleaned = it.replace(',', '.')
                            if (cleaned.isEmpty() || cleaned == "." || cleaned.toFloatOrNull() != null) {
                                currentWeightInput = cleaned
                            }
                        },
                        label = { Text("Current Weight (kg)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        placeholder = { Text("Enter your current weight") }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val weight = weightGoalInput.toFloatOrNull() ?: 0f
                                viewModel.setWeightGoal(weight)
                            },
                            modifier = Modifier.weight(1f),
                            enabled = weightGoalInput.isNotEmpty()
                        ) {
                            Text("Save Goal")
                        }
                        Button(
                            onClick = {
                                val weight = currentWeightInput.toFloatOrNull() ?: 0f
                                viewModel.setCurrentWeight(weight)
                            },
                            modifier = Modifier.weight(1f),
                            enabled = currentWeightInput.isNotEmpty()
                        ) {
                            Text("Register Weight")
                        }
                    }
                }
            }

            // About Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "Amigo",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Text(
                        text = "Version 1.0.0",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Text(
                        text = "Track your calories and macros by simply taking a photo of your meals. Powered by Google's Gemini AI.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}


