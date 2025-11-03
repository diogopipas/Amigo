package com.amigo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.amigo.navigation.Screen
import com.amigo.ui.screens.*
import com.amigo.ui.theme.AmigoTheme
import com.amigo.viewmodel.SettingsViewModel
import com.amigo.data.MascotMessages
import com.amigo.ui.components.AmigoMascotOverlay
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Disable activity transition animations
        overridePendingTransition(0, 0)
        
        setContent {
            val viewModel: SettingsViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsState()
            
            val isDarkTheme = when (themeMode) {
                "dark" -> true
                "light" -> false
                "system" -> isSystemInDarkTheme()
                else -> isSystemInDarkTheme()
            }
            
            AmigoTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AmigoApp()
                }
            }
        }
    }
    
    override fun finish() {
        super.finish()
        // Disable exit animation
        overridePendingTransition(0, 0)
    }
}

@Composable
fun AmigoApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var showWelcome by rememberSaveable { mutableStateOf(true) }

    Scaffold(
        bottomBar = {
            if (currentRoute != null && !currentRoute.startsWith("details/")) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = currentRoute == Screen.Main.route,
                        onClick = {
                            if (currentRoute != Screen.Main.route) {
                                navController.navigate(Screen.Main.route) {
                                    popUpTo(Screen.Main.route) { inclusive = true }
                                }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.History, contentDescription = "History") },
                        label = { Text("History") },
                        selected = currentRoute == Screen.History.route,
                        onClick = {
                            if (currentRoute != Screen.History.route) {
                                navController.navigate(Screen.History.route) {
                                    popUpTo(Screen.Main.route)
                                }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.BarChart, contentDescription = "Statistics") },
                        label = { Text("Stats") },
                        selected = currentRoute == Screen.Statistics.route,
                        onClick = {
                            if (currentRoute != Screen.Statistics.route) {
                                navController.navigate(Screen.Statistics.route) {
                                    popUpTo(Screen.Main.route)
                                }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") },
                        selected = currentRoute == Screen.Settings.route,
                        onClick = {
                            if (currentRoute != Screen.Settings.route) {
                                navController.navigate(Screen.Settings.route) {
                                    popUpTo(Screen.Main.route)
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        if (showWelcome) {
            LaunchedEffect(Unit) {
                delay(2000)
                showWelcome = false
            }
            AmigoMascotOverlay(
                message = "Welcome! " + MascotMessages.randomAny(),
                onDismiss = { showWelcome = false }
            )
        }

        NavHost(
            navController = navController,
            startDestination = Screen.Main.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Main.route) {
                MainScreen(
                    onNavigateToDetails = { mealId ->
                        navController.navigate(Screen.Details.createRoute(mealId))
                    }
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    onMealClick = { mealId ->
                        navController.navigate(Screen.Details.createRoute(mealId))
                    }
                )
            }

            composable(Screen.Statistics.route) {
                StatisticsScreen()
            }

            composable(
                route = Screen.Details.route,
                arguments = listOf(
                    navArgument("mealId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val mealId = backStackEntry.arguments?.getInt("mealId") ?: 0
                DetailsScreen(
                    mealId = mealId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}

