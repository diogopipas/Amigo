package com.amigo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.amigo.ui.screens.*

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object History : Screen("history")
    object Settings : Screen("settings")
    object Details : Screen("details/{mealId}") {
        fun createRoute(mealId: Int) = "details/$mealId"
    }
}

@Composable
fun AmigoNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
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
        
        composable(
            route = Screen.Details.route,
            arguments = listOf(navArgument("mealId") { type = androidx.navigation.NavType.IntType })
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

