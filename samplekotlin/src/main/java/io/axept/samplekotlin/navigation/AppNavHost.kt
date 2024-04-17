package io.axept.samplekotlin.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.axept.samplekotlin.screen.MainScreen
import io.axept.samplekotlin.screen.WebViewScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = HomeDestination.route) {
        composable(route = HomeDestination.route) {

            MainScreen(
                onOpenWebView = { navController.navigate(WebViewDestination.route) }
            )
        }

        composable(route = WebViewDestination.route) {
            WebViewScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}