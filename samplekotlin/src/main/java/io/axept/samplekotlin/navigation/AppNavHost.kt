package io.axept.samplekotlin.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import io.axept.android.library.AxeptioService
import io.axept.samplekotlin.screen.MainScreen
import io.axept.samplekotlin.screen.WebViewScreen

@Composable
internal fun AppNavHost(navController: NavHostController, targetService: AxeptioService) {
    NavHost(navController = navController, startDestination = HomeDestination.route) {

        composable(route = HomeDestination.route) {
            MainScreen(
                targetService = targetService,
                onOpenWebView = { token ->
                    val tokenArg = token.ifBlank { null }
                    navController.navigate(WebViewDestination.route + "/$tokenArg")
                }
            )
        }

        composable(
            route = WebViewDestination.route + "/{${ScreenArguments.TOKEN.slug}}",
            arguments = listOf(navArgument(ScreenArguments.TOKEN.slug) {
                type = NavType.StringType
                defaultValue = null
                nullable = true
            })
        ) {backStackEntry ->
            WebViewScreen(
                onBack = { navController.popBackStack() },
                customToken = backStackEntry.arguments?.getString(ScreenArguments.TOKEN.slug)
            )
        }
    }
}